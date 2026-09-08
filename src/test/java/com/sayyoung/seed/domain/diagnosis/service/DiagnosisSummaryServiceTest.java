package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * V14 마이그레이션이 적재하는 사용자 2 / 목표 3(카테고리 코드 '23')을 기준으로,
 * 새로 생성한 진단에 로드맵을 저장한 뒤 요약 계산을 검증한다. 새로 생성한 진단은
 * created_at이 "지금"이므로 경과 개월 수를 0으로 고정해서 검증할 수 있다.
 */
@SpringBootTest
class DiagnosisSummaryServiceTest {

    private static final Long SEEDED_USER_ID = 2L;
    private static final Long NOT_EXISTING_DIAGNOSIS_ID = 999_999L;

    @Autowired
    private DiagnosisSummaryService diagnosisSummaryService;

    @Autowired
    private DiagnosisResultService diagnosisResultService;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 주_단위와_개월_단위가_섞인_추천항목으로_목표_경과_남은_개월수와_이번달_목표_저축액을_계산한다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));
        String itemKey = "summary_test_" + UUID.randomUUID();
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "23",
                      "order_no": 1,
                      "start_offset": { "value": 2, "unit": "week" },
                      "duration": { "value": 5, "unit": "month" },
                      "title": "테스트 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 1200000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(itemKey);
        diagnosisResultService.applyRoadmap(diagnosis.getId(), rawJson);

        try {
            // when
            DiagnosisSummaryResponse response = diagnosisSummaryService.getSummary(diagnosis.getId());

            // then: 시작 오프셋 2주(올림 1개월) + 기간 5개월 = 목표 6개월, 방금 생성한 진단이라 경과 0개월
            assertThat(response.getTargetMonths()).isEqualTo(6);
            assertThat(response.getElapsedMonths()).isZero();
            assertThat(response.getRemainingMonths()).isEqualTo(6);
            // 사용자 2의 fixed_budget이 NULL이라 누적액 0 기준: 1,200,000 / 6 = 200,000
            assertThat(response.getMonthlyTargetSaving()).isEqualByComparingTo(BigDecimal.valueOf(200_000));
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }

    @Test
    void amount_type가_saving이_아닌_추천항목은_이번달_목표_저축액_합산에서_제외한다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));
        String savingItemKey = "saving_item_" + UUID.randomUUID();
        String expenseItemKey = "expense_item_" + UUID.randomUUID();
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "23",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "month" },
                      "duration": { "value": 4, "unit": "month" },
                      "title": "저축 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 500000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    },
                    {
                      "item_key": "%s",
                      "origin_sub_category": "23",
                      "order_no": 2,
                      "start_offset": { "value": 0, "unit": "month" },
                      "duration": { "value": 4, "unit": "month" },
                      "title": "지출 감소 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 300000,
                      "amount_type": "expense",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(savingItemKey, expenseItemKey);
        diagnosisResultService.applyRoadmap(diagnosis.getId(), rawJson);

        try {
            // when
            DiagnosisSummaryResponse response = diagnosisSummaryService.getSummary(diagnosis.getId());

            // then: expense 300,000은 합산에서 빠지고 saving 500,000만 4개월로 나뉜다 (125,000)
            assertThat(response.getMonthlyTargetSaving()).isEqualByComparingTo(BigDecimal.valueOf(125_000));
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }

    @Test
    void 누적액이_목표금액보다_크면_이번달_목표_저축액은_음수가_아니라_0이다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        user.updateProfile(null, null, BigDecimal.valueOf(5_000_000), null, null, null, null);
        userRepository.save(user);

        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));
        String itemKey = "over_budget_item_" + UUID.randomUUID();
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "23",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "month" },
                      "duration": { "value": 4, "unit": "month" },
                      "title": "저축 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 1000000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(itemKey);
        diagnosisResultService.applyRoadmap(diagnosis.getId(), rawJson);

        try {
            // when
            DiagnosisSummaryResponse response = diagnosisSummaryService.getSummary(diagnosis.getId());

            // then: 누적액 5,000,000 > 목표금액 1,000,000이라 음수가 아니라 0
            assertThat(response.getMonthlyTargetSaving()).isEqualByComparingTo(BigDecimal.ZERO);
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
            user.updateProfile(null, null, null, null, null, null, null);
            userRepository.save(user);
        }
    }

    @Test
    void 연관된_추천이_없으면_목표_개월수는_0이고_이번달_목표_저축액은_null이다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));

        try {
            // when
            DiagnosisSummaryResponse response = diagnosisSummaryService.getSummary(diagnosis.getId());

            // then
            assertThat(response.getTargetMonths()).isZero();
            assertThat(response.getRemainingMonths()).isZero();
            assertThat(response.getMonthlyTargetSaving()).isNull();
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }

    @Test
    void 존재하지_않는_진단이면_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> diagnosisSummaryService.getSummary(NOT_EXISTING_DIAGNOSIS_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND);
    }

    @Test
    void 사용자의_가장_최근_진단을_기준으로_내_로드맵을_계산한다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));
        String itemKey = "my_roadmap_item_" + UUID.randomUUID();
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "23",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "month" },
                      "duration": { "value": 5, "unit": "month" },
                      "title": "테스트 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 1000000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(itemKey);
        diagnosisResultService.applyRoadmap(diagnosis.getId(), rawJson);

        try {
            // when: 방금 생성한 진단이 이 사용자의 최신 진단이 된다
            MyRoadmapResponse response = diagnosisSummaryService.getMyRoadmap(SEEDED_USER_ID);

            // then
            assertThat(response.getSummary().getTargetMonths()).isEqualTo(5);
            assertThat(response.getSummary().getTotalCost()).isEqualByComparingTo(BigDecimal.valueOf(1_000_000));
            // 사용자 2의 fixed_budget이 NULL이라 0
            assertThat(response.getSummary().getSecuredAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            // 사용자 2의 protection_end_date가 NULL이라 protectionEndYm도 null
            assertThat(response.getProtectionEndYm()).isNull();
            assertThat(response.getPlanUntilYm()).isNotNull();
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }

    @Test
    void 진단이_없는_사용자는_내_로드맵_조회시_예외를_던진다() {

        // given
        String loginId = "rm_" + UUID.randomUUID().toString().substring(0, 8);
        User user = userRepository.save(User.create(loginId, "테스트", "20000101", "01000000000", "대학재학"));

        try {
            // when & then
            assertThatThrownBy(() -> diagnosisSummaryService.getMyRoadmap(user.getId()))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND);
        } finally {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    void 존재하지_않는_사용자는_내_로드맵_조회시_인증_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> diagnosisSummaryService.getMyRoadmap(NOT_EXISTING_DIAGNOSIS_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(CommonErrorCode.UNAUTHORIZED);
    }
}

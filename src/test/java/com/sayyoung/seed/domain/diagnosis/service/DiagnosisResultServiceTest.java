package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * V14 마이그레이션이 적재하는 로컬·개발 테스트용 샘플 데이터(사용자 2, 진단 2, 목표 3 - 카테고리 '23'/'교육 지원금')를
 * 기준으로 검증한다.
 */
@SpringBootTest
class DiagnosisResultServiceTest {

    private static final Long SEEDED_USER_ID = 2L;
    private static final Long SEEDED_DIAGNOSIS_ID = 2L;

    @Autowired
    private DiagnosisResultService diagnosisResultService;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 로드맵_응답을_저장하고_진단을_완료상태로_전환한다() {

        // given
        String itemKey = "education_fund_test_" + UUID.randomUUID();
        String checklistKey = "apply_test_" + UUID.randomUUID();
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "교육 지원금",
                      "order_no": 2,
                      "start_offset": { "value": 1, "unit": "month" },
                      "duration": { "value": 2, "unit": "month" },
                      "title": "테스트 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 500000,
                      "amount_type": "expense",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": [
                        { "item_key": "%s", "content": "테스트 체크리스트", "amount_type": "expense", "estimated_amount": 200000 }
                      ]
                    }
                  ]
                }
                """.formatted(itemKey, checklistKey);

        // when
        diagnosisResultService.applyRoadmap(SEEDED_DIAGNOSIS_ID, rawJson);

        // then
        Diagnosis diagnosis = diagnosisRepository.findById(SEEDED_DIAGNOSIS_ID).orElseThrow();
        assertThat(diagnosis.getStatus()).isEqualTo("completed");

        List<Recommendation> recommendations = recommendationRepository.findByDiagnosisId(SEEDED_DIAGNOSIS_ID);
        Recommendation saved = recommendations.stream()
                .filter(r -> r.getItemKey().equals(itemKey))
                .findFirst()
                .orElseThrow();
        assertThat(saved.getGoal().getId()).isNotNull();
        assertThat(saved.getStartOffsetValue()).isEqualTo(1);
        assertThat(saved.getDurationUnit()).isEqualTo("month");

        List<ChecklistItem> checklistItems = checklistItemRepository.findByRecommendationId(saved.getId());
        assertThat(checklistItems).hasSize(1);
        assertThat(checklistItems.get(0).getItemKey()).isEqualTo(checklistKey);
        assertThat(checklistItems.get(0).getOrderNo()).isEqualTo((short) 1);
        assertThat(checklistItems.get(0).getStatus()).isEqualTo("todo");
    }

    @Test
    void 일치하는_카테고리가_없으면_진단을_실패상태로_전환하고_예외를_던진다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));
        String rawJson = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "unknown_category_item",
                      "origin_sub_category": "존재하지-않는-카테고리",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "week" },
                      "duration": { "value": 1, "unit": "month" },
                      "title": "테스트",
                      "content": "테스트",
                      "target_amount": null,
                      "amount_type": null,
                      "target_condition": null,
                      "next_action": "테스트",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """;

        try {
            // when & then
            assertThatThrownBy(() -> diagnosisResultService.applyRoadmap(diagnosis.getId(), rawJson))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(DiagnosisErrorCode.CATEGORY_NOT_FOUND);

            Diagnosis failed = diagnosisRepository.findById(diagnosis.getId()).orElseThrow();
            assertThat(failed.getStatus()).isEqualTo("failed");
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }
}

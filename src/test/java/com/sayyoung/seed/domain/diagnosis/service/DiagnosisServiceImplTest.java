package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.client.DifyClient;
import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisStatusResponse;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.repository.UserGoalRepository;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 테스트 카테고리 코드 '42'(적금, V5 마이그레이션 시드)를 기준으로 검증한다.
 * DifyClient는 실제 네트워크를 타지 않도록 Mockito로 대체한다.
 */
@SpringBootTest
class DiagnosisServiceImplTest {

    private static final String CATEGORY_CODE = "42";

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGoalRepository userGoalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @MockitoBean
    private DifyClient difyClient;

    @Test
    void Dify_호출과_저장이_모두_성공하면_진단을_완료상태로_전환하고_ID를_반환한다() {

        // given
        User user = createUser();
        userGoalRepository.save(UserGoal.create(user, findCategory(), "{}"));
        String itemKey = "test_item_" + UUID.randomUUID();
        when(difyClient.run(any())).thenReturn(rawDifyResponse(itemKey));

        Long diagnosisId = null;
        try {
            // when
            diagnosisId = diagnosisService.diagnose(user.getId(), requestDto());

            // then
            assertThat(diagnosisId).isNotNull();
            Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId).orElseThrow();
            assertThat(diagnosis.getStatus()).isEqualTo("completed");

            List<Recommendation> recommendations = recommendationRepository.findByDiagnosisId(diagnosisId);
            assertThat(recommendations).anyMatch(r -> r.getItemKey().equals(itemKey));
        } finally {
            cleanUp(user, diagnosisId);
        }
    }

    @Test
    void Dify_호출_자체가_실패하면_예외를_전파하고_진단을_실패상태로_남긴다() {

        // given
        User user = createUser();
        userGoalRepository.save(UserGoal.create(user, findCategory(), "{}"));
        when(difyClient.run(any())).thenThrow(new BusinessException(DiagnosisErrorCode.DIFY_API_CALL_FAILED));

        Long diagnosisId = null;
        try {
            // when & then
            assertThatThrownBy(() -> diagnosisService.diagnose(user.getId(), requestDto()))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(DiagnosisErrorCode.DIFY_API_CALL_FAILED);

            Diagnosis diagnosis = diagnosisRepository.findFirstByUserOrderByCreatedAtDesc(user).orElseThrow();
            diagnosisId = diagnosis.getId();
            assertThat(diagnosis.getStatus()).isEqualTo("failed");
        } finally {
            cleanUp(user, diagnosisId);
        }
    }

    @Test
    void 상태_조회는_저장된_진단_상태를_그대로_반환한다() {

        // given
        User user = createUser();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));

        try {
            // when
            DiagnosisStatusResponse response = diagnosisService.getDiagnosis(diagnosis.getId());

            // then
            assertThat(response.getDiagnosisId()).isEqualTo(diagnosis.getId());
            assertThat(response.getStatus()).isEqualTo("running");
        } finally {
            cleanUp(user, diagnosis.getId());
        }
    }

    @Test
    void 존재하지_않는_진단을_조회하면_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> diagnosisService.getDiagnosis(999_999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND);
    }

    private User createUser() {
        return userRepository.save(User.create(
                "diag_test_" + UUID.randomUUID().toString().substring(0, 8),
                "테스트유저",
                LocalDate.of(2001, 1, 1),
                "01000000000"
        ));
    }

    private Category findCategory() {
        return categoryRepository.findById(CATEGORY_CODE).orElseThrow();
    }

    private IntakeRequest requestDto() {
        String json = """
                {
                  "user_profile": {
                    "protection_end_date": "2026-12-31",
                    "is_youth_support": true,
                    "fixed_budget": 1000000,
                    "region_code": "11",
                    "region_display": "서울",
                    "income": 2000000,
                    "is_basic_recipient": false,
                    "household_size": 1
                  },
                  "goals": [
                    {
                      "parent_category_id": "4",
                      "parent_category_id_display": "금융 지원",
                      "category_id": "%s",
                      "category_id_display": "적금",
                      "description": { "reason": "테스트" }
                    }
                  ]
                }
                """.formatted(CATEGORY_CODE);

        return objectMapper.readValue(json, IntakeRequest.class);
    }

    private String rawDifyResponse(String itemKey) {
        return """
                {
                  "roadmap_items": [
                    {
                      "item_key": "%s",
                      "origin_sub_category": "%s",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "week" },
                      "duration": { "value": 1, "unit": "month" },
                      "title": "테스트 추천 항목",
                      "content": "테스트 상세 내용",
                      "target_amount": 100000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트 다음 행동",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(itemKey, CATEGORY_CODE);
    }

    private void cleanUp(User user, Long diagnosisId) {
        if (diagnosisId != null) {
            for (Recommendation recommendation : recommendationRepository.findByDiagnosisId(diagnosisId)) {
                List<ChecklistItem> checklistItems = checklistItemRepository.findByRecommendationId(recommendation.getId());
                checklistItemRepository.deleteAll(checklistItems);
            }
            recommendationRepository.deleteAll(recommendationRepository.findByDiagnosisId(diagnosisId));
            diagnosisRepository.deleteById(diagnosisId);
        }
        userGoalRepository.deleteAllByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }
}

package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.diagnosis.client.DifyClient;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.repository.UserGoalRepository;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V14 마이그레이션이 적재하는 로컬·개발 테스트용 샘플 데이터(진단 1 - 시작오프셋/기간이
 * 각각 0주+3개월, 1주+6개월인 추천 2건 → 목표 개월 수 7)를 기준으로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DiagnosisControllerTest {

    private static final Long SEEDED_DIAGNOSIS_ID = 1L;
    private static final Long NOT_EXISTING_DIAGNOSIS_ID = 999_999L;
    private static final String CATEGORY_CODE = "42";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

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
    void 진단_요약을_정상적으로_조회한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}/summary", SEEDED_DIAGNOSIS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetMonths").value(7));
    }

    @Test
    void 존재하지_않는_진단이면_404를_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}/summary", NOT_EXISTING_DIAGNOSIS_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND.getCode()));
    }

    @Test
    void 진단_요청이_성공하면_결과_조회_URI로_303_리다이렉트한다() throws Exception {

        // given
        User user = userRepository.save(User.create(
                "diag_ctrl_" + UUID.randomUUID().toString().substring(0, 8),
                "테스트유저",
                LocalDate.of(2001, 1, 1),
                "01000000000"
        ));
        String accessToken = jwtProvider.createAccessToken(user.getId());
        userGoalRepository.save(UserGoal.create(user, categoryRepository.findById(CATEGORY_CODE).orElseThrow(), "{}"));
        when(difyClient.run(any())).thenReturn("""
                {
                  "roadmap_items": [
                    {
                      "item_key": "ctrl_test_item",
                      "origin_sub_category": "%s",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "week" },
                      "duration": { "value": 1, "unit": "month" },
                      "title": "테스트",
                      "content": "테스트",
                      "target_amount": 100000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "테스트",
                      "citation": null,
                      "checklist": []
                    }
                  ]
                }
                """.formatted(CATEGORY_CODE));
        String requestBody = """
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

        Long diagnosisId = null;
        try {
            // when & then
            String location = mockMvc.perform(post("/api/diagnoses")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().exists("Location"))
                    .andReturn()
                    .getResponse()
                    .getHeader("Location");

            diagnosisId = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

            mockMvc.perform(get(location))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.diagnosisId").value(diagnosisId))
                    .andExpect(jsonPath("$.data.status").value("completed"));
        } finally {
            cleanUp(user, diagnosisId);
        }
    }

    @Test
    void 진단_상태_조회는_시드된_진단의_상태를_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}", SEEDED_DIAGNOSIS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.diagnosisId").value(SEEDED_DIAGNOSIS_ID))
                .andExpect(jsonPath("$.data.status").value("completed"));
    }

    @Test
    void 존재하지_않는_진단을_상태_조회하면_404를_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}", NOT_EXISTING_DIAGNOSIS_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND.getCode()));
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

package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V14 마이그레이션이 적재하는 사용자 1(진단 1 - 시작오프셋/기간이 각각 0주+3개월,
 * 1주+6개월인 추천 2건 → 목표 개월 수 7)을 기준으로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoadmapControllerTest {

    private static final Long SEEDED_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long SEEDED_RECOMMENDATION_ID = 1L;
    private static final Long SEEDED_TODO_CHECKLIST_ITEM_ID = 2L;
    private static final Long NOT_EXISTING_CHECKLIST_ITEM_ID = 999_999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Test
    void 내_로드맵을_정상적으로_조회한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        mockMvc.perform(get("/api/users/me/roadmap")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.summary.targetMonths").value(7));
    }

    @Test
    void 인증_토큰이_없으면_401을_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/users/me/roadmap"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void 정상적으로_체크리스트_항목을_완료_처리한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);
        Recommendation recommendation = recommendationRepository.findById(SEEDED_RECOMMENDATION_ID).orElseThrow();
        ChecklistItem checklistItem = checklistItemRepository.save(
                ChecklistItem.create(recommendation, "test_complete_item", "테스트 체크리스트 항목", (short) 99, null)
        );

        try {
            // when & then
            mockMvc.perform(post("/api/users/me/roadmap/checklist-items/{itemId}/complete", checklistItem.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNoContent());

            ChecklistItem completed = checklistItemRepository.findById(checklistItem.getId()).orElseThrow();
            assertThat(completed.getStatus()).isEqualTo("done");
            assertThat(completed.getCompletedAt()).isNotNull();
        } finally {
            checklistItemRepository.deleteById(checklistItem.getId());
        }
    }

    @Test
    void 다른_사용자가_체크리스트_항목을_완료하면_403을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(OTHER_USER_ID);

        // when & then
        mockMvc.perform(post(
                        "/api/users/me/roadmap/checklist-items/{itemId}/complete",
                        SEEDED_TODO_CHECKLIST_ITEM_ID
                )
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.FORBIDDEN.getCode()));
    }

    @Test
    void 존재하지_않는_체크리스트_항목이면_404를_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        mockMvc.perform(post(
                        "/api/users/me/roadmap/checklist-items/{itemId}/complete",
                        NOT_EXISTING_CHECKLIST_ITEM_ID
                )
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(DiagnosisErrorCode.CHECKLIST_ITEM_NOT_FOUND.getCode()));
    }

    @Test
    void 인증_토큰_없이_체크리스트_항목을_완료하면_401을_반환한다() throws Exception {

        // when & then
        mockMvc.perform(post(
                        "/api/users/me/roadmap/checklist-items/{itemId}/complete",
                        SEEDED_TODO_CHECKLIST_ITEM_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }
}

package com.sayyoung.seed.domain.savings.controller;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.savings.repository.SavingsRepository;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V18 마이그레이션이 적재하는 저축/예산 배분 샘플 데이터(user_id=1: housing 카테고리(리프 '12')에
 * 저축 2건 500,000원씩, budget_allocations user_amount 1,350,000원)를 기준으로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SavingsControllerTest {

    private static final Long SEEDED_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private SavingsRepository savingsRepository;

    @Test
    void 모은_돈_요약을_정상적으로_조회한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        mockMvc.perform(get("/api/users/me/savings")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(1_000_000))
                .andExpect(jsonPath("$.data.categories.length()").value(2))
                .andExpect(jsonPath("$.data.categories[0].key").value("housing"))
                .andExpect(jsonPath("$.data.categories[0].saved").value(1_000_000))
                .andExpect(jsonPath("$.data.categories[0].goal").value(1_350_000))
                .andExpect(jsonPath("$.data.categories[1].key").value("work"));
    }

    @Test
    void 토큰_없이_모은_돈을_조회하면_401을_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/users/me/savings"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void housing_카테고리_상세를_정상적으로_조회한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        mockMvc.perform(get("/api/users/me/savings/{category}", "housing")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.key").value("housing"))
                .andExpect(jsonPath("$.data.saved").value(1_000_000))
                .andExpect(jsonPath("$.data.goal").value(1_350_000))
                .andExpect(jsonPath("$.data.records.items.length()").value(2))
                .andExpect(jsonPath("$.data.trend.length()").value(2));
    }

    @Test
    void 잘못된_카테고리로_상세를_조회하면_400을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        mockMvc.perform(get("/api/users/me/savings/{category}", "invalid")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 저축_내역을_등록하면_201과_생성된_내역을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);
        String requestBody = """
                {
                    "category": "housing",
                    "item": "테스트 저축 내역",
                    "amount": 10000,
                    "date": "2026-09-09"
                }
                """;

        Long createdId = null;
        try {
            // when
            String response = mockMvc.perform(post("/api/users/me/savings")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(SuccessCode.COMMON_CREATED.getCode()))
                    .andExpect(jsonPath("$.data.category").value("housing"))
                    .andExpect(jsonPath("$.data.item").value("테스트 저축 내역"))
                    .andExpect(jsonPath("$.data.amount").value(10000))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Number id = com.jayway.jsonpath.JsonPath.read(response, "$.data.id");
            createdId = id.longValue();
        } finally {
            if (createdId != null) {
                savingsRepository.deleteById(createdId);
            }
        }
    }

    @Test
    void 금액이_0이하면_400을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);
        String requestBody = """
                {
                    "category": "housing",
                    "item": "잘못된 금액",
                    "amount": 0
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/me/savings")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()));
    }
}

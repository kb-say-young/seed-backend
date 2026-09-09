package com.sayyoung.seed.domain.fundplan.controller;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V18 마이그레이션이 적재하는 예산 배분 샘플 데이터(user_id=1: housing 리프 '12' pct 45.00/budget 1200000/amount 1350000,
 * saving 리프 '42' pct 20.00/budget 600000/amount 600000, work/living 데이터 없음)를 기준으로 검증한다.
 *
 * PUT 성공 경로(실제 배분 반영 로직)는 공유 개발 DB의 시드 데이터를 변경하지 않기 위해
 * {@code FundPlanServiceTest}(Mockito 단위 테스트)에서만 검증하고, 여기서는 라우팅/인증/검증 실패만 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FundPlanControllerTest {

    private static final Long SEEDED_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void 예산_현황을_정상적으로_조회한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);

        // when & then
        // 버킷 순서는 FundPlanBucket enum 선언 순서(HOUSING, LIVING, WORK, SAVING)를 따른다.
        mockMvc.perform(get("/api/users/me/fund-plan")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.buckets.length()").value(4))
                .andExpect(jsonPath("$.data.buckets[0].key").value("housing"))
                .andExpect(jsonPath("$.data.buckets[0].pct").value(45.00))
                .andExpect(jsonPath("$.data.buckets[0].budget").value(1200000))
                .andExpect(jsonPath("$.data.buckets[0].amount").value(1350000))
                .andExpect(jsonPath("$.data.buckets[0].actual").value(1000000))
                .andExpect(jsonPath("$.data.buckets[3].key").value("saving"))
                .andExpect(jsonPath("$.data.buckets[3].pct").value(20.00))
                .andExpect(jsonPath("$.data.buckets[3].budget").value(600000))
                .andExpect(jsonPath("$.data.buckets[3].actual").value(300000))
                .andExpect(jsonPath("$.data.principles.length()").value(2));
    }

    @Test
    void 토큰_없이_예산을_조회하면_401을_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/users/me/fund-plan"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void 배분_비율_합계가_100이_아니면_400을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);
        String requestBody = """
                {
                    "allocations": [
                        {"key": "housing", "pct": 30},
                        {"key": "work", "pct": 30}
                    ]
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/me/fund-plan/allocation")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 잘못된_카테고리_키면_400을_반환한다() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken(SEEDED_USER_ID);
        String requestBody = """
                {
                    "allocations": [
                        {"key": "invalid", "pct": 100}
                    ]
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/me/fund-plan/allocation")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 토큰_없이_배분을_수정하면_401을_반환한다() throws Exception {

        // given
        String requestBody = """
                {
                    "allocations": [
                        {"key": "housing", "pct": 100}
                    ]
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/me/fund-plan/allocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }
}

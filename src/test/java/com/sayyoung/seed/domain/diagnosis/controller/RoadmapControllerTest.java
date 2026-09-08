package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

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
}

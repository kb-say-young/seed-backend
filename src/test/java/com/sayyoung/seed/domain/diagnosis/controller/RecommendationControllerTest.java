package com.sayyoung.seed.domain.diagnosis.controller;

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
 * V14 마이그레이션이 적재하는 로컬·개발 테스트용 샘플 데이터(진단 1 - 월세/적금 추천 2건)를 기준으로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecommendationControllerTest {

    private static final Long SEEDED_DIAGNOSIS_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 진단_로드맵_목록을_정상적으로_조회한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}/recommendations", SEEDED_DIAGNOSIS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void 잘못된_category_값이면_400을_반환한다() throws Exception {

        // when & then
        mockMvc.perform(get("/api/diagnoses/{diagnosisId}/recommendations", SEEDED_DIAGNOSIS_ID)
                        .param("category", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAMETER.getCode()));
    }
}

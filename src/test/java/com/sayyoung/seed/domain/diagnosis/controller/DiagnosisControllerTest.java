package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private MockMvc mockMvc;

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
}

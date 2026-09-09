package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisStatusResponse;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;

/**
 * 진단 생성과 결과 조회 기능을 제공하는 서비스 인터페이스.
 */
public interface DiagnosisService {

    /**
     * 사용자 최신 정보를 반영하고 AI 진단을 수행한다.
     *
     * @return 저장된 진단 ID
     */
    Long diagnose(
            Long userId,
            IntakeRequest requestDto
    );

    /**
     * 진단의 현재 상태를 조회한다.
     */
    DiagnosisStatusResponse getDiagnosis(Long diagnosisId);
}

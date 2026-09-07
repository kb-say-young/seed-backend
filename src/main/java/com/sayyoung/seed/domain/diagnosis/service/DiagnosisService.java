package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.request.DiagnosisRequestDto;

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
            DiagnosisRequestDto requestDto
    );


}

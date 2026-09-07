package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 진단 실패 상태 기록을 별도 트랜잭션으로 분리합니다.
 * 로드맵 저장 트랜잭션이 롤백되더라도 실패 상태는 반드시 커밋되어야 하기 때문이다.
 */
@Component
@RequiredArgsConstructor
class DiagnosisFailureRecorder {

    private final DiagnosisRepository diagnosisRepository;

    // 프록시 기반 @Transactional은 public 메서드에만 적용되므로 반드시 public이어야 한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long diagnosisId) {
        diagnosisRepository.findById(diagnosisId).ifPresent(Diagnosis::fail);
    }
}

package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.client.DifyClient;
import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisStatusResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.mapper.DifyWorkflowMapper;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 사용자 정보 갱신부터 Dify 진단까지의 전체 흐름을 처리한다.
 */
@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final UserRepository userRepository;
    private final DiagnosisRepository diagnosisRepository;

    // Dify 요청 DTO 변환
    private final DifyWorkflowMapper difyWorkflowMapper;

    // Dify Workflow API 호출
    private final DifyClient difyClient;

    // Dify 응답 파싱·저장 및 진단 완료 처리
    private final DiagnosisResultService diagnosisResultService;

    // Dify 호출 자체가 실패했을 때 진단을 실패 상태로 기록
    private final DiagnosisFailureRecorder diagnosisFailureRecorder;

    // 메서드 전체를 하나의 트랜잭션으로 묶지 않는다: Dify 호출은 느린 외부 HTTP 호출이라
    // 그 구간 동안 DB 트랜잭션을 붙잡고 있으면 안 되고, 아래 각 단계(Diagnosis 저장,
    // markFailed, applyRoadmap)가 각자 독립된 트랜잭션으로 커밋되어야
    // Dify 호출 실패 시 markFailed가 이미 커밋된 Diagnosis 행을 찾을 수 있다.
    @Override
    public Long diagnose(
            Long userId,
            IntakeRequest requestDto
    ) {

        // 진단 대상 사용자 조회
        User user = userRepository.findById(userId).orElseThrow();

        // 진단을 running 상태로 생성·저장하여 diagnosisId 확보
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));

        // Dify Workflow 요청 DTO 생성
        DifyWorkflowRequestDto difyWorkflowRequest = difyWorkflowMapper.toRequest(
                user.getId(),
                requestDto
        );

        // Dify Workflow 호출
        String rawDifyResponse;
        try {
            rawDifyResponse = difyClient.run(difyWorkflowRequest);
        } catch (RuntimeException e) {
            diagnosisFailureRecorder.markFailed(diagnosis.getId());
            throw e;
        }

        // 진단 결과 저장 및 완료 처리 (실패 시 내부에서 실패 상태로 기록됨)
        diagnosisResultService.applyRoadmap(diagnosis.getId(), rawDifyResponse);

        // 진단 결과 PK 반환
        return diagnosis.getId();
    }

    @Override
    public DiagnosisStatusResponse getDiagnosis(Long diagnosisId) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        return DiagnosisStatusResponse.of(diagnosis.getId(), diagnosis.getStatus());
    }
}

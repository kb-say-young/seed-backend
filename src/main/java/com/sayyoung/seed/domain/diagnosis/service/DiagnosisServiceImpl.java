package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.client.DifyClient;
import com.sayyoung.seed.domain.diagnosis.dto.request.DiagnosisRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.response.DifyWorkflowResponseDto;
import com.sayyoung.seed.domain.diagnosis.mapper.DifyWorkflowMapper;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 정보 갱신부터 Dify 진단까지의 전체 흐름을 처리한다.
 */
@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final UserRepository userRepository;

    // Dify 요청 DTO 변환
    private final DifyWorkflowMapper difyWorkflowMapper;

    // Dify Workflow API 호출
    private final DifyClient difyClient;

    @Override
    @Transactional
    public Long diagnose(
            Long userId,
            DiagnosisRequestDto requestDto
    ) {

        // 진단 대상 사용자 조회
        User user = userRepository.findById(userId).orElseThrow();

        // Dify Workflow 요청 DTO 생성
        DifyWorkflowRequestDto difyWorkflowRequest = difyWorkflowMapper.toRequest(
                user.getId(),
                requestDto
        );

        // Dify Workflow 호출
        DifyWorkflowResponseDto difyWorkflowResponse = difyClient.run(difyWorkflowRequest);

        // TODO: 진단 결과 저장

        // TODO: 진단 결과 PK 반환

        return null;
    }
}

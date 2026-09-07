package com.sayyoung.seed.domain.diagnosis.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayyoung.seed.domain.diagnosis.dto.request.DiagnosisRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 진단 요청 데이터를 Dify Workflow 요청 형식으로 변환한다.
 */
@Component
@RequiredArgsConstructor
public class DifyWorkflowMapper {

    private final ObjectMapper objectMapper;

    public DifyWorkflowRequestDto toRequest(
            DiagnosisRequestDto requestDto,
            Long userId
    ) {
        try {
            // 사용자 정보를 JSON 문자열로 변환
            String userContext = objectMapper.writeValueAsString(
                    requestDto.getUserProfileDto()
            );

            // 목표 목록을 JSON 문자열로 변환
            String goalsInput = objectMapper.writeValueAsString(
                    requestDto.getGoals()
            );

            return DifyWorkflowRequestDto.of(
                    userContext,
                    goalsInput,
                    userId
            );
        } catch (JsonProcessingException e) {
            throw new BusinessException(
                    DiagnosisErrorCode.DIFY_REQUEST_SERIALIZATION_FAILED
            );
        }
    }
}

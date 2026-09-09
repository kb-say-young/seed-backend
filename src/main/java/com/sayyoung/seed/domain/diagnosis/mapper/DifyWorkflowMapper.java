package com.sayyoung.seed.domain.diagnosis.mapper;


import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class DifyWorkflowMapper {

    private final ObjectMapper objectMapper;

    public DifyWorkflowRequestDto toRequest(
            Long userId,
            IntakeRequest requestDto
    ) {
        try {
            String userContext = objectMapper.writeValueAsString(
                    requestDto.getUserProfile()
            );

            String goalsInput = objectMapper.writeValueAsString(
                    requestDto.getGoals()
            );

            return DifyWorkflowRequestDto.of(
                    userContext,
                    goalsInput,
                    userId
            );

        } catch (JacksonException e) {
            throw new BusinessException(
                    DiagnosisErrorCode.DIFY_REQUEST_SERIALIZATION_FAILED
            );
        }
    }
}

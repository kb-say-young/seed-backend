package com.sayyoung.seed.domain.diagnosis.client;

import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.response.DifyWorkflowResponseDto;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Dify Workflow API 호출을 담당한다.
 */
@Component
public class DifyClient {

    // Dify Workflow 실행 경로
    private static final String WORKFLOW_PATH = "/v1/workflows/run";

    private final RestClient difyRestClient;

    public DifyClient(@Qualifier("difyRestClient") RestClient difyRestClient) {
        this.difyRestClient = difyRestClient;
    }

    /**
     * Dify Workflow를 실행한다.
     */
    public DifyWorkflowResponseDto run(DifyWorkflowRequestDto requestDto) {
        try {
            DifyWorkflowResponseDto response = difyRestClient
                    .post()
                    .uri(WORKFLOW_PATH)
                    .body(requestDto)
                    .retrieve()
                    .body(DifyWorkflowResponseDto.class);

            if (response == null) {
                throw new BusinessException(DiagnosisErrorCode.DIFY_RESPONSE_EMPTY);
            }

            return response;
        } catch (RestClientException e) {
            throw new BusinessException(DiagnosisErrorCode.DIFY_API_CALL_FAILED);
        }
    }
}
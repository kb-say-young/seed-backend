package com.sayyoung.seed.domain.diagnosis.client;

import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Dify Workflow API 호출을 담당한다.
 */
@Slf4j
@Component
public class DifyClient {

    // Dify Workflow 실행 경로
    private static final String WORKFLOW_PATH = "/v1/workflows/run";

    private final RestClient difyRestClient;

    public DifyClient(@Qualifier("difyRestClient") RestClient difyRestClient) {
        this.difyRestClient = difyRestClient;
    }

    /**
     * Dify Workflow를 실행하고 원본 응답 JSON 문자열을 반환한다.
     */
    public String run(DifyWorkflowRequestDto requestDto) {
        try {
            String response = difyRestClient
                    .post()
                    .uri(WORKFLOW_PATH)
                    .body(requestDto)
                    .retrieve()
                    .body(String.class);

            if (response == null || response.isBlank()) {
                throw new BusinessException(DiagnosisErrorCode.DIFY_RESPONSE_EMPTY);
            }

            return response;
        } catch (RestClientResponseException e) {
            // Dify가 4xx/5xx로 응답한 경우 원본 응답 바디(에러 사유)를 로그로 남긴다.
            log.warn(
                    "Dify Workflow API 호출 실패: status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
            throw new BusinessException(DiagnosisErrorCode.DIFY_API_CALL_FAILED);
        } catch (RestClientException e) {
            log.warn("Dify Workflow API 호출 실패", e);
            throw new BusinessException(DiagnosisErrorCode.DIFY_API_CALL_FAILED);
        }
    }
}
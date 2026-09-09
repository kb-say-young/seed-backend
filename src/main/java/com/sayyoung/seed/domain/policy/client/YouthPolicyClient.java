package com.sayyoung.seed.domain.policy.client;

import com.sayyoung.seed.domain.policy.dto.response.YouthPolicyApiDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 청년정책 Open API를 호출합니다.
 */
@Component
@RequiredArgsConstructor
public class YouthPolicyClient {

    // 정책 상세 조회 API 경로
    private static final String POLICY_DETAIL_PATH = "/go/ythip/getPlcy";

    // API Key 파라미터명
    private static final String API_KEY_PARAM = "apiKeyNm";

    // 조회 유형 파라미터명
    private static final String PAGE_TYPE_PARAM = "pageType";

    // 정책번호 파라미터명
    private static final String POLICY_NO_PARAM = "plcyNo";

    // 응답 타입 파라미터명
    private static final String RETURN_TYPE_PARAM = "rtnType";

    // 상세 조회 유형
    private static final String PAGE_TYPE = "1";

    // JSON 응답
    private static final String RETURN_TYPE = "json";

    private final RestClient youthPolicyRestClient;

    @Value("${youth-policy.api-key}")
    private String apiKey;

    /**
     * 정책번호를 기준으로 정책 상세 정보를 조회합니다.
     */
    public YouthPolicyApiDetailResponseDto getPolicyDetail(String policyNo) {
        return youthPolicyRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(POLICY_DETAIL_PATH)
                        .queryParam(API_KEY_PARAM, apiKey)
                        .queryParam(PAGE_TYPE_PARAM, PAGE_TYPE)
                        .queryParam(POLICY_NO_PARAM, policyNo)
                        .queryParam(RETURN_TYPE_PARAM, RETURN_TYPE)
                        .build()
                )
                .retrieve()
                .body(YouthPolicyApiDetailResponseDto.class);
    }
}

package com.sayyoung.seed.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 청년정책 Open API RestClient를 설정합니다.
 */
@Configuration
public class YouthPolicyConfig {

    /**
     * 청년정책 Open API 호출용 RestClient를 생성합니다.
     */
    @Bean
    public RestClient youthPolicyRestClient(
            @Value("${youth-policy.base-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}

package com.sayyoung.seed.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

/**
 * Dify API 호출을 위한 설정을 구성한다.
 */
@Configuration
public class DifyConfig {

    // Authorization 헤더 Bearer 접두사
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    /**
     * Dify API 호출용 RestClient를 생성한다.
     */
    @Bean
    public RestClient difyRestClient(
            @Value("${dify.base-url}") String baseUrl,
            @Value("${dify.api-key}") String apiKey
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,AUTHORIZATION_PREFIX + apiKey
                )
                .build();
    }
}

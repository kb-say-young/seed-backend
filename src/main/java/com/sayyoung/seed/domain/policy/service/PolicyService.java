package com.sayyoung.seed.domain.policy.service;

import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;

import java.util.List;

/**
 * 정책 관련 기능을 제공하는 서비스 인터페이스.
 */
public interface PolicyService {

    /**
     * 카테고리와 사용자 정보를 기준으로 맞춤 정책을 조회한다.
     */
    List<PolicyRecommendationResponseDto> getRecommendations(
            Long userId,
            String categoryId
    );

}

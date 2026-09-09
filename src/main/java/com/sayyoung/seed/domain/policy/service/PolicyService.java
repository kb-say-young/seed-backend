package com.sayyoung.seed.domain.policy.service;

import com.sayyoung.seed.domain.policy.dto.response.PolicyDetailResponseDto;
import com.sayyoung.seed.domain.policy.dto.response.YouthPolicyApiDetailResponseDto;
import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import org.springframework.data.domain.Page;

/**
 * 정책 조회 비즈니스 로직을 정의한다.
 */
public interface PolicyService {

    /**
     * 사용자 정보와 카테고리를 기준으로 맞춤 정책을 페이지 단위로 조회한다.
     */
    Page<PolicyRecommendationResponseDto> getRecommendations(
            Long userId,
            String categoryId,
            int page,
            int size
    );

    /**
     * 정책 상세 정보를 조회합니다.
     */
    PolicyDetailResponseDto getPolicyDetail(
            Long policyId
    );
}
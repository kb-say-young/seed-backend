package com.sayyoung.seed.domain.policy.controller;

import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import com.sayyoung.seed.domain.policy.service.PolicyService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 사용자 맞춤 정책 조회를 처리하는 컨트롤러.
 */
@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    // 사용자 맞춤 정책 조회 서비스
    private final PolicyService policyService;

    /**
     * 카테고리와 사용자 정보를 기준으로 맞춤 정책을 조회한다.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<PolicyRecommendationResponseDto>>> getRecommendations(
            @AuthenticationPrincipal Long userId,
            @RequestParam String categoryId
    ) {
        List<PolicyRecommendationResponseDto> response = policyService.getRecommendations(
                userId,
                categoryId
        );

        return ResponseFactory.success(
                SuccessCode.POLICY_RECOMMENDATION_READ_SUCCESS,
                response
        );
    }
}

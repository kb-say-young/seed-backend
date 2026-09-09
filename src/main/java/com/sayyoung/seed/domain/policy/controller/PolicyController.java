package com.sayyoung.seed.domain.policy.controller;

import com.sayyoung.seed.domain.policy.dto.response.PolicyDetailResponseDto;
import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import com.sayyoung.seed.domain.policy.service.PolicyService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.PageApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 정책 관련 API를 처리합니다.
 */
@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    /**
     * 사용자 정보를 기반으로 맞춤 정책을 페이지 단위로 조회합니다.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<PageApiResponse<PolicyRecommendationResponseDto>> getRecommendations(
            @AuthenticationPrincipal Long userId,
            @RequestParam String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PolicyRecommendationResponseDto> response =
                policyService.getRecommendations(
                        userId,
                        categoryId,
                        page,
                        size
                );

        return ResponseFactory.pageSuccess(
                SuccessCode.POLICY_RECOMMENDATION_READ_SUCCESS,
                response
        );
    }

    /**
     * 정책 ID를 기준으로 정책 상세 정보를 조회합니다.
     */
    @GetMapping("/{policyId}")
    public ResponseEntity<ApiResponse<PolicyDetailResponseDto>> getPolicyDetail(
            @PathVariable Long policyId
    ) {
        PolicyDetailResponseDto response = policyService.getPolicyDetail(policyId);

        return ResponseFactory.success(
                SuccessCode.POLICY_DETAIL_READ_SUCCESS,
                response
        );
    }
}
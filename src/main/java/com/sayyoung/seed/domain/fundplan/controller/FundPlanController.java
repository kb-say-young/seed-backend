package com.sayyoung.seed.domain.fundplan.controller;

import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;
import com.sayyoung.seed.domain.fundplan.service.FundPlanService;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 예산(fund-plan, B1) 조회/배분 수정 API를 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/fund-plan")
public class FundPlanController implements FundPlanControllerDocs {

    private final FundPlanService fundPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<FundPlanResponse>> getFundPlan(
            @AuthenticationPrincipal Long userId
    ) {
        requireAuthenticated(userId);

        FundPlanResponse response = fundPlanService.getFundPlan(userId);
        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @PutMapping("/allocation")
    public ResponseEntity<ApiResponse<FundPlanResponse>> updateAllocation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody FundPlanAllocationUpdateRequest request
    ) {
        requireAuthenticated(userId);

        FundPlanResponse response = fundPlanService.updateAllocation(userId, request);
        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    private void requireAuthenticated(
            Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
    }
}

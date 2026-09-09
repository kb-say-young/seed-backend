package com.sayyoung.seed.domain.savings.controller;

import com.sayyoung.seed.domain.savings.dto.request.SavingsCreateRequest;
import com.sayyoung.seed.domain.savings.dto.response.SavingsCreateResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsDetailResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsSummaryResponse;
import com.sayyoung.seed.domain.savings.service.SavingsService;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 저축(모은 돈) 조회/등록 API를 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/savings")
public class SavingsController implements SavingsControllerDocs {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final SavingsService savingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<SavingsSummaryResponse>> getSavings(
            @AuthenticationPrincipal Long userId
    ) {
        requireAuthenticated(userId);

        SavingsSummaryResponse response = savingsService.getSavings(userId);
        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponse<SavingsDetailResponse>> getSavingsDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        requireAuthenticated(userId);

        Pageable pageable = PageRequest.of(page, size > 0 ? size : DEFAULT_PAGE_SIZE);
        SavingsDetailResponse response = savingsService.getSavingsDetail(userId, category, pageable);
        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SavingsCreateResponse>> createSaving(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody SavingsCreateRequest request
    ) {
        requireAuthenticated(userId);

        SavingsCreateResponse response = savingsService.createSaving(userId, request);
        return ResponseFactory.success(SuccessCode.COMMON_CREATED, response);
    }

    private void requireAuthenticated(
            Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
    }
}

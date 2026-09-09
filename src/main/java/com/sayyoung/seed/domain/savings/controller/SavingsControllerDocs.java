package com.sayyoung.seed.domain.savings.controller;

import com.sayyoung.seed.domain.savings.dto.request.SavingsCreateRequest;
import com.sayyoung.seed.domain.savings.dto.response.SavingsCreateResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsDetailResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsSummaryResponse;
import com.sayyoung.seed.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 저축(모은 돈) 조회/등록 API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "저축(모은 돈) API",
        description = "A1 모은 돈 요약, A2 카테고리별 내역, S3 내역 작성 API"
)
public interface SavingsControllerDocs {

    @Operation(
            summary = "모은 돈(A1) 요약 조회",
            description = "인증된 사용자의 housing/work 카테고리 저축 현황을 합산한 전체 누적액, 저축 속도(pace), "
                    + "카테고리별 요약을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "모은 돈 요약 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<SavingsSummaryResponse>> getSavings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "카테고리별(A2) 저축 상세 조회",
            description = "housing 또는 work 카테고리의 목표/누적/이번 달 목표, 월별 누적 추이(trend), "
                    + "저축 내역 목록(records, 페이지네이션)을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카테고리 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "category 값이 housing|work 중 어디에도 해당하지 않음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<SavingsDetailResponse>> getSavingsDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId,

            @Parameter(
                    name = "category",
                    description = "조회할 카테고리 (housing: 주거, work: 취·창업)",
                    example = "housing",
                    required = true
            )
            String category,

            @Parameter(
                    name = "page",
                    description = "페이지 번호(0부터 시작)",
                    example = "0"
            )
            int page,

            @Parameter(
                    name = "size",
                    description = "페이지 크기",
                    example = "20"
            )
            int size
    );

    @Operation(
            summary = "저축 내역 등록 (S3)",
            description = "housing 또는 work 카테고리에 새 저축 내역을 등록합니다. date를 생략하면 오늘 날짜로 저장됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "저축 내역 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패 또는 잘못된 category 값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<SavingsCreateResponse>> createSaving(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId,

            @Valid @RequestBody SavingsCreateRequest request
    );
}

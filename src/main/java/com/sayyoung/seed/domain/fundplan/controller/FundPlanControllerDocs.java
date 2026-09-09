package com.sayyoung.seed.domain.fundplan.controller;

import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;
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
 * 예산(fund-plan, B1) 조회/배분 수정 API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "예산(fund-plan) API",
        description = "B1 예산 조회 및 배분 수정 API"
)
public interface FundPlanControllerDocs {

    @Operation(
            summary = "예산(B1) 조회",
            description = "전체 예상 비용/확보액/부족분과 housing·living·work·saving 4개 버킷별 배분 현황(AI 추천, 확정 배분, "
                    + "실제 반영액), 배분 원칙 점검 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "예산 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "진단 정보가 없는 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<FundPlanResponse>> getFundPlan(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "예산 배분 수정 (B1 '수정' 모드)",
            description = "housing|living|work|saving 카테고리별 배분 비율을 수정합니다. 비율 합계가 100이 아니면 저장하지 않고, "
                    + "성공 시 갱신된 예산 현황을 그대로 반환합니다(재조회 불필요)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "배분 수정 성공, 갱신된 예산 현황 반환"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패, 잘못된 카테고리 키, 또는 배분 비율 합계가 100이 아님"
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
    ResponseEntity<ApiResponse<FundPlanResponse>> updateAllocation(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId,

            @Valid @RequestBody FundPlanAllocationUpdateRequest request
    );
}

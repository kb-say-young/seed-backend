package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.dto.request.RecommendationCategory;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationResponse;
import com.sayyoung.seed.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 로드맵(추천) 조회 API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "로드맵 API",
        description = "진단 결과로 생성된 추천(로드맵) 항목 조회 API"
)
public interface RecommendationControllerDocs {

    /**
     * 진단에 연관된 추천(로드맵) 목록 조회 API 명세입니다.
     *
     * @param diagnosisId 조회할 진단 식별자
     * @param category    상위 카테고리 필터, 전달되지 않으면 전체 조회
     * @return 공통 응답 형식으로 감싼 추천 목록
     */
    @Operation(
            summary = "로드맵(추천) 목록 조회",
            description = "진단 식별자를 기준으로 해당 진단에서 생성된 추천(로드맵) 항목 목록을 조회합니다. "
                    + "category 파라미터를 전달하면 상위 카테고리(주거/생활/취업·창업/금융) 기준으로 필터링하며, "
                    + "전달하지 않으면 전체 추천 항목을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로드맵(추천) 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "category 파라미터 값이 올바르지 않음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 진단 식별자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @Parameter(
                    name = "diagnosisId",
                    description = "조회할 진단 식별자",
                    example = "1",
                    required = true
            )
            Long diagnosisId,

            @Parameter(
                    name = "category",
                    description = "상위 카테고리 필터(HOUSING: 주거, LIVING: 생활, JOB_STARTUP: 취업·창업, FINANCE: 금융). "
                            + "전달하지 않으면 전체 조회합니다."
            )
            RecommendationCategory category
    );
}

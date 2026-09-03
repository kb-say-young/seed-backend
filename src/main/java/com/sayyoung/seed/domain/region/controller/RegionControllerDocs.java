package com.sayyoung.seed.domain.region.controller;

import com.sayyoung.seed.domain.region.dto.response.SidoResponse;
import com.sayyoung.seed.domain.region.dto.response.SigunguResponse;
import com.sayyoung.seed.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 지역 조회 API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "지역 API",
        description = "시도 및 시군구 조회 API"
)
public interface RegionControllerDocs {

    /**
     * 전체 시도 목록 조회 API 명세입니다.
     *
     * @return 공통 응답 형식으로 감싼 시도 목록
     */
    @Operation(
            summary = "시도 목록 조회",
            description = "전체 시도 목록을 지역 코드 순서로 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "시도 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<List<SidoResponse>>> getSidos();


    /**
     * 특정 시도에 포함된 시군구 목록 조회 API 명세입니다.
     *
     * @param sidoCode 조회할 시도의 지역 코드
     * @return 공통 응답 형식으로 감싼 시군구 목록
     */
    @Operation(
            summary = "시군구 목록 조회",
            description = "시도 코드를 기준으로 해당 시도에 포함된 시군구 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "시군구 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 지역 코드"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    ResponseEntity<ApiResponse<List<SigunguResponse>>> getSigungus(
            @Parameter(
                    name = "sidoCode",
                    description = "조회할 5자리 지역 코드",
                    example = "11000",
                    required = true
            )
            String sidoCode
    );
}

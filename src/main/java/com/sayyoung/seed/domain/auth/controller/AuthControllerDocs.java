package com.sayyoung.seed.domain.auth.controller;

import com.sayyoung.seed.domain.auth.dto.request.RefreshTokenRequest;
import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

/**
 * 인증(토큰) API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "인증 API",
        description = "JWT 토큰 재발급 및 로그아웃 API"
)
public interface AuthControllerDocs {

    /**
     * Refresh Token Rotation(RTR) 방식의 토큰 재발급 API 명세입니다.
     *
     * @param request 리프레시 토큰 요청
     * @return 공통 응답 형식으로 감싼 새 액세스/리프레시 토큰
     */
    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 검증한 뒤 Redis에 저장된 토큰과 일치하는 경우 새 액세스/리프레시 토큰을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰"
            )
    })
    ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @Valid RefreshTokenRequest request
    );

    /**
     * 로그아웃 API 명세입니다.
     *
     * @param request 리프레시 토큰 요청
     * @return 데이터가 없는 공통 응답
     */
    @Operation(
            summary = "로그아웃",
            description = "Redis에 저장된 리프레시 토큰을 삭제하여 로그아웃 처리합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰"
            )
    })
    ResponseEntity<ApiResponse<Void>> logout(
            @Valid RefreshTokenRequest request
    );
}

package com.sayyoung.seed.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰을 필요로 하는 요청(재발급, 로그아웃)의 DTO입니다.
 */
@Schema(description = "리프레시 토큰 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRequest {

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}

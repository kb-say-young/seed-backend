package com.sayyoung.seed.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 액세스/리프레시 토큰 발급 결과를 반환하는 DTO입니다.
 */
@Schema(description = "토큰 발급 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private final String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private final String refreshToken;

    /**
     * 액세스/리프레시 토큰으로 응답 DTO를 생성합니다.
     *
     * @param accessToken  발급된 액세스 토큰
     * @param refreshToken 발급된 리프레시 토큰
     * @return 생성된 토큰 응답 DTO
     */
    public static TokenResponse of(
            String accessToken,
            String refreshToken
    ) {
        return new TokenResponse(accessToken, refreshToken);
    }
}

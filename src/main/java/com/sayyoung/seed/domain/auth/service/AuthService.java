package com.sayyoung.seed.domain.auth.service;

import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.auth.exception.AuthErrorCode;
import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.auth.jwt.JwtTokenType;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토큰 재발급 및 로그아웃과 관련된 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * Refresh Token Rotation(RTR) 방식으로 액세스/리프레시 토큰을 재발급합니다.
     *
     * @param refreshToken 클라이언트가 보유한 리프레시 토큰
     * @return 새로 발급된 액세스/리프레시 토큰
     */
    public TokenResponse reissue(
            String refreshToken
    ) {
        validateRefreshToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);
        refreshTokenService.validateRotation(userId, refreshToken);

        String newAccessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenService.save(userId, newRefreshToken);

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    /**
     * 저장된 리프레시 토큰을 삭제하여 로그아웃 처리합니다.
     *
     * @param refreshToken 클라이언트가 보유한 리프레시 토큰
     */
    public void logout(
            String refreshToken
    ) {
        validateRefreshToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);
        refreshTokenService.delete(userId);
    }

    private void validateRefreshToken(
            String refreshToken
    ) {
        boolean isValidRefreshToken = jwtProvider.validateToken(refreshToken)
                && jwtProvider.getTokenType(refreshToken) == JwtTokenType.REFRESH;

        if (!isValidRefreshToken) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}

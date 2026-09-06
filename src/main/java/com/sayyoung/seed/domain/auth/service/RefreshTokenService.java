package com.sayyoung.seed.domain.auth.service;

import com.sayyoung.seed.domain.auth.exception.AuthErrorCode;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis를 이용해 리프레시 토큰을 저장하고 Refresh Token Rotation(RTR)을 검증합니다.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * 사용자의 리프레시 토큰을 저장(갱신)합니다.
     *
     * @param userId       사용자 식별자
     * @param refreshToken 저장할 리프레시 토큰
     */
    public void save(
            Long userId,
            String refreshToken
    ) {
        redisTemplate.opsForValue().set(
                key(userId),
                refreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );
    }

    /**
     * 전달받은 리프레시 토큰이 Redis에 저장된 토큰과 일치하는지 검증합니다.
     * 일치하지 않으면 토큰 탈취로 간주하여 저장된 토큰을 즉시 폐기합니다.
     *
     * @param userId       사용자 식별자
     * @param refreshToken 검증할 리프레시 토큰
     * @throws BusinessException 저장된 토큰이 없거나 일치하지 않는 경우
     */
    public void validateRotation(
            Long userId,
            String refreshToken
    ) {
        String savedToken = redisTemplate.opsForValue().get(key(userId));

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            delete(userId);
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * 사용자의 리프레시 토큰을 삭제합니다.
     *
     * @param userId 사용자 식별자
     */
    public void delete(
            Long userId
    ) {
        redisTemplate.delete(key(userId));
    }

    private String key(
            Long userId
    ) {
        return KEY_PREFIX + userId;
    }
}

package com.sayyoung.seed.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 액세스/리프레시 토큰의 발급과 검증을 담당합니다.
 */
@Component
public class JwtProvider {

    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * 액세스 토큰을 발급합니다.
     *
     * @param userId 토큰 주체가 될 사용자 식별자
     * @return 발급된 액세스 토큰
     */
    public String createAccessToken(
            Long userId
    ) {
        return createToken(userId, JwtTokenType.ACCESS, accessTokenExpiration);
    }

    /**
     * 리프레시 토큰을 발급합니다.
     *
     * @param userId 토큰 주체가 될 사용자 식별자
     * @return 발급된 리프레시 토큰
     */
    public String createRefreshToken(
            Long userId
    ) {
        return createToken(userId, JwtTokenType.REFRESH, refreshTokenExpiration);
    }

    private String createToken(
            Long userId,
            JwtTokenType tokenType,
            long expiration
    ) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_TOKEN_TYPE, tokenType.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 사용자 식별자를 추출합니다.
     *
     * @param token 파싱할 토큰
     * @return 토큰에 담긴 사용자 식별자
     */
    public Long getUserId(
            String token
    ) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 토큰의 종류(액세스/리프레시)를 추출합니다.
     *
     * @param token 파싱할 토큰
     * @return 토큰 종류
     */
    public JwtTokenType getTokenType(
            String token
    ) {
        String type = parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
        return JwtTokenType.valueOf(type);
    }

    /**
     * 토큰의 서명과 만료 여부를 검증합니다.
     *
     * @param token 검증할 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(
            String token
    ) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(
            String token
    ) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

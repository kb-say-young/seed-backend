package com.sayyoung.seed.domain.auth.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 인증 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorResponseCode {

    /**
     * 리프레시 토큰의 서명/만료가 유효하지 않거나, Redis에 저장된 토큰과 일치하지 않는 경우 발생합니다.
     */
    INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_401_001",
            "유효하지 않은 리프레시 토큰입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

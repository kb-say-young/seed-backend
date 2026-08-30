package com.sayyoung.seed.domain.user.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 사용자 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorResponseCode {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404_001",
            "사용자를 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

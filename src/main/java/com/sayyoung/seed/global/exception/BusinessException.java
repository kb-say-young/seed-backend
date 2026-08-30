package com.sayyoung.seed.global.exception;

import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외를 표현합니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorResponseCode errorCode;

    /**
     * 에러 코드를 기반으로 비즈니스 예외를 생성합니다.
     */
    public BusinessException(
            ErrorResponseCode errorCode
    ) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

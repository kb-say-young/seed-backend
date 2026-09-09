package com.sayyoung.seed.domain.savings.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 저축(savings) 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum SavingsErrorCode implements ErrorResponseCode {

    /**
     * category 경로 변수/요청 값이 housing|work 중 어디에도 해당하지 않는 경우 발생합니다.
     */
    INVALID_CATEGORY(
            HttpStatus.BAD_REQUEST,
            "SAVINGS_400_001",
            "지원하지 않는 카테고리입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

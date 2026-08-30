package com.sayyoung.seed.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;

/**
 * API 예외 응답의 공통 형식을 정의합니다.
 */
@Getter
@JsonPropertyOrder({"code", "message"})
public class ErrorResponse {

    private final String code;
    private final String message;

    /**
     * 외부에서 직접 생성하지 못하도록 생성자를 제한합니다.
     */
    private ErrorResponse(
            String code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }

    /**
     * ErrorCode의 기본 메시지를 사용하는 에러 응답을 생성합니다.
     */
    public static ErrorResponse of(
            ErrorResponseCode errorCode
    ) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }

    /**
     * 별도의 메시지를 사용하는 에러 응답을 생성합니다.
     */
    public static ErrorResponse of(
            ErrorResponseCode errorCode,
            String message
    ) {
        return new ErrorResponse(
                errorCode.getCode(),
                message
        );
    }
}

package com.sayyoung.seed.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 성공 응답 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResponseCode {

    COMMON_OK(
            HttpStatus.OK,
            "COMMON_200",
            "요청에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

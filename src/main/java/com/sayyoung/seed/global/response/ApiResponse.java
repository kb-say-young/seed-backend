package com.sayyoung.seed.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.Getter;

/**
 * 성공 API 응답의 공통 형식을 정의합니다.
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@JsonPropertyOrder({"success", "code", "message", "data"})
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    /**
     * 외부에서 직접 생성하지 못하도록 생성자를 제한합니다.
     */
    private ApiResponse(
            boolean success,
            String code,
            String message,
            T data
    ) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 데이터를 포함한 성공 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> success(
            SuccessCode successCode,
            T data
    ) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                data
        );
    }

    /**
     * 데이터가 없는 성공 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> success(
            SuccessCode successCode
    ) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                null
        );
    }
}

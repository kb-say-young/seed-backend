package com.sayyoung.seed.global.response;

import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

/**
 * HTTP 상태 코드와 응답 본문을 조합하여
 * ResponseEntity를 생성합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseFactory {

    /**
     * 데이터를 포함한 성공 응답을 생성합니다.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(
            SuccessCode successCode,
            T data
    ) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(ApiResponse.success(successCode, data));
    }

    /**
     * 데이터가 없는 성공 응답을 생성합니다.
     */
    public static ResponseEntity<ApiResponse<Void>> success(
            SuccessCode successCode
    ) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(ApiResponse.success(successCode));
    }

    /**
     * 기본 메시지를 사용하는 에러 응답을 생성합니다.
     */
    public static ResponseEntity<ApiResponse<Void>> failure(
            ErrorResponseCode errorCode
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    /**
     * 커스텀 메시지를 사용하는 에러 응답을 생성합니다.
     */
    public static ResponseEntity<ApiResponse<Void>> failure(
            ErrorResponseCode errorCode,
            String message
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode, message));
    }
}

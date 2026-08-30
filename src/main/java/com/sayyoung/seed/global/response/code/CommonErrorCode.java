package com.sayyoung.seed.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 실패 응답 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorResponseCode {

    // 400 Bad Request
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "COMMON_400_001",
            "잘못된 요청입니다."
    ),

    INVALID_REQUEST_BODY(
            HttpStatus.BAD_REQUEST,
            "COMMON_400_002",
            "요청 본문의 형식이 올바르지 않습니다."
    ),

    VALIDATION_FAILED(
            HttpStatus.BAD_REQUEST,
            "COMMON_400_003",
            "요청 값 검증에 실패했습니다."
    ),

    INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "COMMON_400_004",
            "요청 파라미터의 형식이 올바르지 않습니다."
    ),

    MISSING_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "COMMON_400_005",
            "필수 요청 파라미터가 누락되었습니다."
    ),

    // 401 Unauthorized
    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "COMMON_401_001",
            "인증이 필요합니다."
    ),

    // 403 Forbidden
    FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "COMMON_403_001",
            "접근 권한이 없습니다."
    ),

    // 404 Not Found
    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMON_404_001",
            "요청한 리소스를 찾을 수 없습니다."
    ),

    // 409 Conflict
    CONFLICT(
            HttpStatus.CONFLICT,
            "COMMON_409_001",
            "요청이 현재 상태와 충돌합니다."
    ),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON_500_001",
            "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

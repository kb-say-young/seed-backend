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
    ),

    COMMON_CREATED(
            HttpStatus.CREATED,
            "COMMON_201",
            "리소스가 생성되었습니다."
    ),

    POLICY_RECOMMENDATION_READ_SUCCESS(
            HttpStatus.OK,
            "POLICY_200_001",
            "맞춤 정책 조회에 성공했습니다."
    ),

    POLICY_DETAIL_READ_SUCCESS(
            HttpStatus.OK,
        "POLICY_200_002",
                "정책 상세 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

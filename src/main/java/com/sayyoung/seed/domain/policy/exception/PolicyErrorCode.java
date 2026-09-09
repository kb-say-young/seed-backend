package com.sayyoung.seed.domain.policy.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PolicyErrorCode implements ErrorResponseCode {

    POLICY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POLICY_404_001",
            "정책을 찾을 수 없습니다."
    ),

    POLICY_DETAIL_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POLICY_404_002",
            "정책 상세 정보를 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

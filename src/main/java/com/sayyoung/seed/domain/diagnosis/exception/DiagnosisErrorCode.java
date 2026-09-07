package com.sayyoung.seed.domain.diagnosis.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 진단 관련 에러 코드.
 */
@Getter
@RequiredArgsConstructor
public enum DiagnosisErrorCode implements ErrorResponseCode {

    DIFY_REQUEST_SERIALIZATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "DIAGNOSIS_500_1",
            "Dify 요청 데이터 변환에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

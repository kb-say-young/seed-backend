package com.sayyoung.seed.domain.region.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RegionErrorCode implements ErrorResponseCode {

    /**
     * 요청한 지역 코드가 존재하지 않는 경우 발생합니다.
     */
    REGION_CODE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "REGION_404_001",
            "존재하지 않는 지역 코드입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

package com.sayyoung.seed.domain.fundplan.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 예산(fund-plan) 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum FundPlanErrorCode implements ErrorResponseCode {

    /**
     * allocations의 key 값이 housing|living|work|saving 중 어디에도 해당하지 않는 경우 발생합니다.
     */
    INVALID_BUCKET(
            HttpStatus.BAD_REQUEST,
            "FUNDPLAN_400_001",
            "지원하지 않는 배분 카테고리입니다."
    ),

    /**
     * allocations의 pct 합계가 100이 아닌 경우 발생합니다.
     */
    ALLOCATION_SUM_INVALID(
            HttpStatus.BAD_REQUEST,
            "FUNDPLAN_400_002",
            "배분 비율의 합계는 100이어야 합니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

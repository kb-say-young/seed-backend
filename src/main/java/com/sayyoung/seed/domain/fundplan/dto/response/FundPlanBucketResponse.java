package com.sayyoung.seed.domain.fundplan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 예산(fund-plan) 버킷 하나(housing|living|work|saving)의 배분 현황 응답 DTO입니다.
 */
@Schema(description = "예산 버킷 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FundPlanBucketResponse {

    @Schema(description = "버킷 키", example = "housing")
    private final String key;

    @Schema(description = "버킷 라벨", example = "주거 지원")
    private final String label;

    @Schema(description = "배분 비율(%)", example = "40.00")
    private final BigDecimal pct;

    @Schema(description = "실제 반영된 금액(savings 누적액)", example = "1000000")
    private final BigDecimal actual;

    @Schema(description = "AI 추천 배분 금액", example = "1200000")
    private final BigDecimal budget;

    @Schema(description = "확정된 배분 금액(사용자 조정분 우선)", example = "1350000")
    private final BigDecimal amount;

    @Schema(description = "목표 배분 기간(개월)", example = "60")
    private final Integer targetMonths;

    public static FundPlanBucketResponse of(
            String key,
            String label,
            BigDecimal pct,
            BigDecimal actual,
            BigDecimal budget,
            BigDecimal amount,
            Integer targetMonths
    ) {
        return new FundPlanBucketResponse(key, label, pct, actual, budget, amount, targetMonths);
    }
}

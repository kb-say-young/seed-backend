package com.sayyoung.seed.domain.fundplan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 예산(fund-plan, B1) 조회/수정 응답 DTO입니다.
 */
@Schema(description = "예산(fund-plan) 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FundPlanResponse {

    @Schema(description = "전체 예상 비용(로드맵 추천 saving 항목 target_amount 합계)", example = "12532000")
    private final BigDecimal totalFund;

    @Schema(description = "확보액(users.fixed_budget)", example = "8000000")
    private final BigDecimal securedAmount;

    @Schema(description = "부족분(totalFund - securedAmount, 0 미만이면 0)", example = "4532000")
    private final BigDecimal shortfallAmount;

    @Schema(description = "버킷(housing|living|work|saving)별 배분 현황")
    private final List<FundPlanBucketResponse> buckets;

    @Schema(description = "배분 원칙 점검 목록")
    private final List<FundPlanPrincipleResponse> principles;

    public static FundPlanResponse of(
            BigDecimal totalFund,
            BigDecimal securedAmount,
            BigDecimal shortfallAmount,
            List<FundPlanBucketResponse> buckets,
            List<FundPlanPrincipleResponse> principles
    ) {
        return new FundPlanResponse(totalFund, securedAmount, shortfallAmount, buckets, principles);
    }
}

package com.sayyoung.seed.domain.diagnosis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 로드맵 화면 상단 런웨이 바에 노출되는 요약 정보 응답 DTO입니다.
 */
@Schema(description = "로드맵 요약 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoadmapSummaryResponse {

    @Schema(description = "목표 개월 수", example = "60")
    private final int targetMonths;

    @Schema(description = "전체 목표 금액(saving 항목 합계)", example = "12532000")
    private final BigDecimal totalCost;

    @Schema(description = "현재 누적액", example = "8000000")
    private final BigDecimal securedAmount;

    public static RoadmapSummaryResponse of(
            int targetMonths,
            BigDecimal totalCost,
            BigDecimal securedAmount
    ) {
        return new RoadmapSummaryResponse(
                targetMonths,
                totalCost,
                securedAmount
        );
    }
}

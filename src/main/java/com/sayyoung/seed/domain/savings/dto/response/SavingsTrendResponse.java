package com.sayyoung.seed.domain.savings.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * A2(카테고리 상세) 응답의 월별 누적 저축 추이 항목입니다.
 */
@Schema(description = "월별 누적 저축 추이")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsTrendResponse {

    @Schema(description = "연-월 (yyyy-MM)", example = "2026-08")
    private final String month;

    @Schema(description = "해당 월까지의 누적 저축액", example = "1000000")
    private final BigDecimal cumulative;

    public static SavingsTrendResponse of(
            String month,
            BigDecimal cumulative
    ) {
        return new SavingsTrendResponse(month, cumulative);
    }
}

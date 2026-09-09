package com.sayyoung.seed.domain.savings.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * A1(모은 돈) 조회 응답 DTO입니다.
 */
@Schema(description = "모은 돈 요약 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsSummaryResponse {

    @Schema(description = "전체 누적 저축액", example = "1300000")
    private final BigDecimal total;

    @Schema(description = "저축 속도 (ahead|onTrack|behind)", example = "onTrack")
    private final String pace;

    @Schema(description = "카테고리별 요약 목록")
    private final List<SavingsCategorySummaryResponse> categories;

    public static SavingsSummaryResponse of(
            BigDecimal total,
            String pace,
            List<SavingsCategorySummaryResponse> categories
    ) {
        return new SavingsSummaryResponse(total, pace, categories);
    }
}

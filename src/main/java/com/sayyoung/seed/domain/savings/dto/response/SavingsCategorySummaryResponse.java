package com.sayyoung.seed.domain.savings.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * A1(모은 돈) 응답에 포함되는 카테고리별 요약입니다.
 */
@Schema(description = "저축 카테고리 요약")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsCategorySummaryResponse {

    @Schema(description = "카테고리 키", example = "housing")
    private final String key;

    @Schema(description = "카테고리 라벨", example = "주거 지원")
    private final String label;

    @Schema(description = "목표 금액", example = "3000000")
    private final BigDecimal goal;

    @Schema(description = "누적 저축액", example = "1000000")
    private final BigDecimal saved;

    @Schema(description = "이번 달 목표 금액", example = "250000")
    private final BigDecimal monthlyGoal;

    @Schema(description = "안내 문구", example = "목표까지 2,000,000원 남았어요.")
    private final String note;

    public static SavingsCategorySummaryResponse of(
            String key,
            String label,
            BigDecimal goal,
            BigDecimal saved,
            BigDecimal monthlyGoal,
            String note
    ) {
        return new SavingsCategorySummaryResponse(key, label, goal, saved, monthlyGoal, note);
    }
}

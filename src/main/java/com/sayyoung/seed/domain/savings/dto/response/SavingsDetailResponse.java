package com.sayyoung.seed.domain.savings.dto.response;

import com.sayyoung.seed.global.response.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * A2(카테고리 상세) 조회 응답 DTO입니다.
 */
@Schema(description = "저축 카테고리 상세 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsDetailResponse {

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

    @Schema(description = "이번 달 저축액", example = "200000")
    private final BigDecimal savedThisMonth;

    @Schema(description = "안내 문구", example = "목표까지 2,000,000원 남았어요.")
    private final String note;

    @Schema(description = "월별 누적 저축 추이")
    private final List<SavingsTrendResponse> trend;

    @Schema(description = "저축 내역 목록(페이지네이션)")
    private final PageResponse<SavingsRecordResponse> records;

    public static SavingsDetailResponse of(
            String key,
            String label,
            BigDecimal goal,
            BigDecimal saved,
            BigDecimal monthlyGoal,
            BigDecimal savedThisMonth,
            String note,
            List<SavingsTrendResponse> trend,
            PageResponse<SavingsRecordResponse> records
    ) {
        return new SavingsDetailResponse(
                key, label, goal, saved, monthlyGoal, savedThisMonth, note, trend, records
        );
    }
}

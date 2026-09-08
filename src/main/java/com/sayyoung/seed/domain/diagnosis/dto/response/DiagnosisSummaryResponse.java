package com.sayyoung.seed.domain.diagnosis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 로드맵 화면 상단에 노출되는 진단 요약 정보 응답 DTO입니다.
 */
@Schema(description = "진단 요약 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiagnosisSummaryResponse {

    /**
     * 로드맵 전체가 목표로 하는 총 개월 수입니다.
     */
    @Schema(description = "목표 개월 수", example = "6")
    private final int targetMonths;

    /**
     * 진단 생성일로부터 경과한 개월 수입니다.
     */
    @Schema(description = "경과 개월 수", example = "2")
    private final int elapsedMonths;

    /**
     * 목표 개월 수에서 경과 개월 수를 뺀 남은 개월 수입니다.
     */
    @Schema(description = "남은 개월 수", example = "4")
    private final int remainingMonths;

    /**
     * 남은 기간 동안 매달 저축해야 할 목표 금액입니다. 남은 개월 수가 0 이하이면 계산할 수 없어 null입니다.
     */
    @Schema(description = "이번달 목표 저축액", example = "125000")
    private final BigDecimal monthlyTargetSaving;

    public static DiagnosisSummaryResponse of(
            int targetMonths,
            int elapsedMonths,
            int remainingMonths,
            BigDecimal monthlyTargetSaving
    ) {
        return new DiagnosisSummaryResponse(
                targetMonths,
                elapsedMonths,
                remainingMonths,
                monthlyTargetSaving
        );
    }
}

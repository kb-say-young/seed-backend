package com.sayyoung.seed.domain.diagnosis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시작 시점 오프셋(start_offset)과 기간(duration)이 공유하는 값-단위 쌍입니다.
 */
@Schema(description = "값-단위 쌍 (start_offset, duration 공용)")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodDto {

    @Schema(description = "값", example = "3")
    private Integer value;

    @Schema(description = "단위", example = "month", allowableValues = {"week", "month"})
    private String unit;
}

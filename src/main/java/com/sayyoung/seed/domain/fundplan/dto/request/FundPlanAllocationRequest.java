package com.sayyoung.seed.domain.fundplan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 배분 항목 하나(카테고리 키 + 비율)를 나타내는 요청 DTO입니다.
 */
@Schema(description = "배분 항목")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundPlanAllocationRequest {

    @Schema(description = "카테고리 키 (housing|living|work|saving)", example = "housing")
    @NotBlank(message = "카테고리 키는 필수입니다.")
    private String key;

    @Schema(description = "배분 비율(%)", example = "40")
    @NotNull(message = "배분 비율은 필수입니다.")
    private BigDecimal pct;
}

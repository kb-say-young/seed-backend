package com.sayyoung.seed.domain.savings.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 저축 내역 등록(S3) 요청 DTO입니다.
 */
@Schema(description = "저축 내역 등록 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsCreateRequest {

    @Schema(description = "카테고리 (housing: 주거, work: 취·창업)", example = "housing")
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @Schema(description = "저축 항목명", example = "4월 월세 적립")
    @NotBlank(message = "항목명은 필수입니다.")
    private String item;

    @Schema(description = "저축 금액(원, 양수)", example = "500000")
    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    private Long amount;

    @Schema(description = "저축일(미입력 시 오늘 날짜)", example = "2026-09-09")
    private LocalDate date;
}

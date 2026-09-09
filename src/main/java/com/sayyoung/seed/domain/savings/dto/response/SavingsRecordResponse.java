package com.sayyoung.seed.domain.savings.dto.response;

import com.sayyoung.seed.domain.savings.entity.Savings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A2(카테고리 상세) 응답의 저축 내역 단건입니다.
 */
@Schema(description = "저축 내역 단건")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsRecordResponse {

    @Schema(description = "저축 내역 식별자", example = "1")
    private final Long id;

    @Schema(description = "저축일", example = "2026-09-05")
    private final LocalDate date;

    @Schema(description = "저축 항목명", example = "4월 월세 적립")
    private final String item;

    @Schema(description = "저축 금액", example = "500000")
    private final BigDecimal amount;

    public static SavingsRecordResponse from(
            Savings saving
    ) {
        return new SavingsRecordResponse(
                saving.getId(),
                saving.getSavedAt(),
                saving.getTitle(),
                saving.getAmount()
        );
    }
}

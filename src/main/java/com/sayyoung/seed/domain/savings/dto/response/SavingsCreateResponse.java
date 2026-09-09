package com.sayyoung.seed.domain.savings.dto.response;

import com.sayyoung.seed.domain.savings.entity.Savings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 저축 내역 등록(S3) 응답 DTO입니다.
 */
@Schema(description = "저축 내역 등록 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SavingsCreateResponse {

    @Schema(description = "생성된 저축 내역 식별자", example = "10")
    private final Long id;

    @Schema(description = "카테고리 키", example = "housing")
    private final String category;

    @Schema(description = "저축 항목명", example = "4월 월세 적립")
    private final String item;

    @Schema(description = "저축 금액", example = "500000")
    private final BigDecimal amount;

    @Schema(description = "저축일", example = "2026-09-09")
    private final LocalDate date;

    public static SavingsCreateResponse of(
            Savings saving,
            String categoryKey
    ) {
        return new SavingsCreateResponse(
                saving.getId(),
                categoryKey,
                saving.getTitle(),
                saving.getAmount(),
                saving.getSavedAt()
        );
    }
}

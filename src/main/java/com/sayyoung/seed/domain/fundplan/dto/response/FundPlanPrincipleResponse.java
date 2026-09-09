package com.sayyoung.seed.domain.fundplan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예산 배분 원칙 점검 항목 응답 DTO입니다.
 */
@Schema(description = "예산 배분 원칙 항목")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FundPlanPrincipleResponse {

    @Schema(description = "원칙 설명", example = "배분 비율 합계가 100%예요")
    private final String text;

    @Schema(description = "충족 여부", example = "true")
    private final boolean ok;

    public static FundPlanPrincipleResponse of(
            String text,
            boolean ok
    ) {
        return new FundPlanPrincipleResponse(text, ok);
    }
}

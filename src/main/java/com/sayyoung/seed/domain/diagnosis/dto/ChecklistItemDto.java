package com.sayyoung.seed.domain.diagnosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추천(로드맵) 항목에 속한 체크리스트 항목 응답 DTO입니다.
 * amount_type은 checklist_items 테이블에 저장할 컬럼이 없어 파싱만 하고 저장하지 않는다.
 */
@Schema(description = "추천 항목의 체크리스트 항목")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChecklistItemDto {

    @Schema(description = "체크리스트 항목 키", example = "open_saving_account")
    @JsonProperty("item_key")
    private String itemKey;

    @Schema(description = "체크리스트 항목 내용", example = "월세 적립 전용 계좌 개설")
    private String content;

    @Schema(description = "금액 유형 (저장하지 않음)", example = "saving", allowableValues = {"saving", "expense", "income"})
    @JsonProperty("amount_type")
    private String amountType;

    @Schema(description = "예상 금액", example = "500000")
    @JsonProperty("estimated_amount")
    private Long estimatedAmount;
}

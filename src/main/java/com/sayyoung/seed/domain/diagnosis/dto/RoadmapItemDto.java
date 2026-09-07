package com.sayyoung.seed.domain.diagnosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 목표 하나에 대한 로드맵(추천) 항목 응답 DTO입니다.
 */
@Schema(description = "사용자 목표 하나에 대한 로드맵(추천) 항목")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapItemDto {

    @Schema(description = "추천 항목 키", example = "housing_rent_saving")
    @JsonProperty("item_key")
    private String itemKey;

    @Schema(description = "원본 세부 카테고리 코드(categories.category_id와 매칭해 사용자 목표를 찾는다)", example = "13")
    @JsonProperty("origin_sub_category")
    private String originSubCategory;

    @Schema(description = "표시 순서", example = "1")
    @JsonProperty("order_no")
    private Integer orderNo;

    @Schema(description = "시작 시점 오프셋")
    @JsonProperty("start_offset")
    private PeriodDto startOffset;

    @Schema(description = "기간")
    private PeriodDto duration;

    @Schema(description = "추천 항목 제목", example = "월세 적립 계획 수립")
    private String title;

    @Schema(description = "추천 항목 상세 내용")
    private String content;

    @Schema(description = "목표 금액", example = "1500000")
    @JsonProperty("target_amount")
    private Long targetAmount;

    @Schema(description = "금액 유형", example = "saving", allowableValues = {"saving", "expense", "income"})
    @JsonProperty("amount_type")
    private String amountType;

    @Schema(description = "목표 달성 조건")
    @JsonProperty("target_condition")
    private String targetCondition;

    @Schema(description = "다음 행동 안내")
    @JsonProperty("next_action")
    private String nextAction;

    @Schema(description = "근거/출처")
    private String citation;

    @Schema(description = "실행 체크리스트")
    private List<ChecklistItemDto> checklist;
}

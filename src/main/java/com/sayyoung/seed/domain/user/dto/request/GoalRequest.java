package com.sayyoung.seed.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 목표 선택 단계에서 제출되는 목표 1건의 요청 DTO입니다 (`카테고리별_요청_API_계약서.md` §3.2).
 * description의 필드 집합은 category_id별로 다르며(§4), 백엔드는 구조를 해석하지 않고 그대로 저장한다.
 */
@Schema(description = "목표 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRequest {

    @Schema(description = "대분류 카테고리 코드(categories.parent_category_id)", example = "1")
    @JsonProperty("parent_category_id")
    @NotBlank(message = "대분류 카테고리 코드는 필수입니다.")
    private String parentCategoryId;

    @Schema(description = "세부 카테고리 코드(categories.category_id)", example = "13")
    @JsonProperty("category_id")
    @NotBlank(message = "세부 카테고리 코드는 필수입니다.")
    private String categoryId;

    @Schema(description = "LLM 입력용 세부 카테고리 자연어 라벨. 생략 시 백엔드가 채운다.", example = "공공임대")
    @JsonProperty("category_id_display")
    private String categoryIdDisplay;

    @Schema(description = "LLM 입력용 대분류 자연어 라벨. 생략 시 백엔드가 채운다.", example = "주거")
    @JsonProperty("parent_category_id_display")
    private String parentCategoryIdDisplay;

    @Schema(description = "세부 카테고리별 추가 질문 응답(계약서 §4 스키마)")
    @NotEmpty(message = "세부 목표 추가 질문 응답은 필수입니다.")
    private Map<String, Object> description;
}

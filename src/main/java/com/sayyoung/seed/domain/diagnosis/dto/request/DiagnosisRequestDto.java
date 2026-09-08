package com.sayyoung.seed.domain.diagnosis.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 사용자 프로필과 목표 정보를 전달받는 진단 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class DiagnosisRequestDto {

    // 사용자 정보
    @Valid
    @NotNull
    @JsonProperty("user_profile")
    private UserProfileDto userProfileDto;

    // 진단에 사용할 목표 목록
    @Valid
    @NotNull
    @JsonProperty("goals")
    private List<GoalDto> goals;

    /**
     * 사용자 프로필 정보.
     */
    @Getter
    @NoArgsConstructor
    public static class UserProfileDto {

        // 보호 종료일
        @NotNull
        @JsonProperty("protection_end_date")
        private LocalDate protectionEndDate;

        // 자립준비청년 여부
        @NotNull
        @JsonProperty("is_youth_support")
        private Boolean isYouthSupport;

        // 현재 보유 자산
        @NotNull
        @PositiveOrZero
        @JsonProperty("fixed_budget")
        private Long fixedBudget;

        // 현재 거주 지역 코드
        @NotBlank
        @JsonProperty("region_code")
        private String regionCode;

        // LLM 전달용 지역명
        @JsonProperty("region_display")
        private String regionDisplay;

        // 월 평균 소득
        @NotNull
        @PositiveOrZero
        @JsonProperty("income")
        private Long income;

        // 기초생활수급자 여부
        @NotNull
        @JsonProperty("is_basic_recipient")
        private Boolean isBasicRecipient;

        // 가구원 수
        @NotNull
        @Min(1)
        @JsonProperty("household_size")
        private Integer householdSize;
    }


    /**
     * AI 진단에 사용하는 하나의 목표 정보.
     */
    @Getter
    @NoArgsConstructor
    public static class GoalDto {

        // 대분류 카테고리 ID
        @NotBlank
        @JsonProperty("parent_category_id")
        private String parentCategoryId;

        // LLM 전달용 대분류명
        @JsonProperty("parent_category_id_display")
        private String parentCategoryIdDisplay;

        // 세부 카테고리 ID
        @NotBlank
        @JsonProperty("category_id")
        private String categoryId;

        // LLM 전달용 세부 카테고리명
        @JsonProperty("category_id_display")
        private String categoryIdDisplay;

        // 카테고리별 세부 목표
        @NotNull
        @JsonProperty("description")
        private Map<String, Object> description;
    }
}

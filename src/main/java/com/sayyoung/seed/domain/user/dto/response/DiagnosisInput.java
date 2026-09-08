package com.sayyoung.seed.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** 매칭 엔진과 LLM 엔진이 공유하는 DB 조회 시점의 사용자 입력. 금액은 원 단위다. */
public record DiagnosisInput(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("user_profile") Profile userProfile,
        List<Goal> goals
) {
    public DiagnosisInput { goals = List.copyOf(goals); }

    public record Profile(
            @JsonProperty("birth_date") String birthDate,
            @JsonProperty("protection_end_date") LocalDate protectionEndDate,
            @JsonProperty("is_youth_support") Boolean youthSupport,
            @JsonProperty("region_code") String regionCode,
            @JsonProperty("region_display") String regionDisplay,
            Long income,
            @JsonProperty("household_size") Short householdSize,
            @JsonProperty("is_basic_recipient") Boolean basicRecipient,
            @JsonProperty("fixed_budget") BigDecimal fixedBudget,
            BigDecimal budget,
            @JsonProperty("has_cda") Boolean hasCda
    ) { }

    /** description은 저장된 키/값/숫자/배열을 변환 없이 전달한다. */
    public record Goal(
            @JsonProperty("goal_id") Long goalId,
            @JsonProperty("parent_category_id") String parentCategoryId,
            @JsonProperty("parent_category_id_display") String parentCategoryDisplay,
            @JsonProperty("category_id") String categoryId,
            @JsonProperty("category_id_display") String categoryDisplay,
            Map<String, Object> description
    ) { }
}

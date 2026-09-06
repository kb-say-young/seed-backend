package com.sayyoung.seed.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 진단 공통 프로필 요청 DTO입니다. `카테고리별_요청_API_계약서.md` §3.1을 따르며,
 * JSON 키는 users 테이블 컬럼명과 동일한 snake_case를 그대로 사용합니다.
 */
@Schema(description = "진단 공통 프로필 요청 (user_profile)")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileRequest {

    @Schema(description = "보호종료(예정)일", example = "2027-03-01")
    @JsonProperty("protection_end_date")
    @NotNull(message = "보호종료(예정)일은 필수입니다.")
    private LocalDate protectionEndDate;

    @Schema(description = "자립준비청년 신청 여부", example = "true")
    @JsonProperty("is_youth_support")
    @NotNull(message = "자립준비청년 신청 여부는 필수입니다.")
    private Boolean youthSupport;

    @Schema(description = "디딤씨앗통장(CDA) 잔액(원)", example = "8000000")
    @JsonProperty("fixed_budget")
    @NotNull(message = "디딤씨앗통장 잔액은 필수입니다.")
    @PositiveOrZero(message = "디딤씨앗통장 잔액은 0 이상이어야 합니다.")
    private Long fixedBudget;

    @Schema(description = "거주지 지역 코드(시/군/구 레벨만 허용)", example = "11530")
    @JsonProperty("region_code")
    @NotBlank(message = "거주지 지역 코드는 필수입니다.")
    private String regionCode;

    @Schema(description = "LLM 입력용 자연어 지역명. 생략 시 백엔드가 region_code로 채운다.", example = "서울특별시 구로구")
    @JsonProperty("region_display")
    private String regionDisplay;

    @Schema(description = "월 평균 소득 금액(원)", example = "1500000")
    @NotNull(message = "월 평균 소득 금액은 필수입니다.")
    @PositiveOrZero(message = "월 평균 소득 금액은 0 이상이어야 합니다.")
    private Long income;

    @Schema(description = "기초생활수급자 여부", example = "false")
    @JsonProperty("is_basic_recipient")
    @NotNull(message = "기초생활수급자 여부는 필수입니다.")
    private Boolean basicRecipient;

    @Schema(description = "가구원 수", example = "1")
    @JsonProperty("household_size")
    @NotNull(message = "가구원 수는 필수입니다.")
    @Positive(message = "가구원 수는 1명 이상이어야 합니다.")
    private Integer householdSize;
}

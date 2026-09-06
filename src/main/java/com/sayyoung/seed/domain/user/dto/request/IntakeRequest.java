package com.sayyoung.seed.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 진단(공통 프로필/목표) 제출 요청 DTO입니다 (`카테고리별_요청_API_계약서.md` §3).
 */
@Schema(description = "진단 정보 제출 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntakeRequest {

    @Schema(description = "공통 프로필")
    @JsonProperty("user_profile")
    @NotNull(message = "user_profile은 필수입니다.")
    @Valid
    private UserProfileRequest userProfile;

    @Schema(description = "선택한 목표 목록")
    @NotEmpty(message = "목표는 최소 1개 이상 선택해야 합니다.")
    @Valid
    private List<GoalRequest> goals;
}

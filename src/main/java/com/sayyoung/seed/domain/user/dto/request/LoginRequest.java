package com.sayyoung.seed.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO입니다. 아이디만으로 로그인합니다.
 */
@Schema(description = "로그인 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @Schema(description = "로그인 아이디", example = "seedyouth")
    @NotBlank(message = "아이디는 필수입니다.")
    private String loginId;
}

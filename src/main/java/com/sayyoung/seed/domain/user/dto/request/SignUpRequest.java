package com.sayyoung.seed.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원가입 요청 DTO입니다.
 */
@Schema(description = "회원가입 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    @Schema(description = "로그인 아이디", example = "seedyouth")
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 30, message = "아이디는 4자 이상 30자 이하로 입력해주세요.")
    private String loginId;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "생년월일(yyyyMMdd)", example = "19990101")
    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "\\d{8}", message = "생년월일은 yyyyMMdd 형식의 숫자 8자리로 입력해주세요.")
    private LocalDate birthDate;

    @Schema(description = "휴대폰 번호(하이픈 제외)", example = "01012345678")
    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "010\\d{8}", message = "휴대폰 번호는 010으로 시작하는 숫자 11자리로 입력해주세요.")
    private String phoneNumber;
}

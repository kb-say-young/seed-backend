package com.sayyoung.seed.domain.user.dto.response;

import com.sayyoung.seed.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원가입 결과를 반환하는 DTO입니다.
 */
@Schema(description = "회원가입 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Schema(description = "사용자 식별자", example = "1")
    private final Long id;

    @Schema(description = "로그인 아이디", example = "seedyouth")
    private final String loginId;

    /**
     * User 엔티티를 회원가입 응답 DTO로 변환합니다.
     *
     * @param user 변환할 사용자 엔티티
     * @return 변환된 회원가입 응답 DTO
     */
    public static UserResponse from(
            User user
    ) {
        return new UserResponse(user.getId(), user.getLoginId());
    }
}

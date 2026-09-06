package com.sayyoung.seed.domain.user.controller;

import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.dto.request.LoginRequest;
import com.sayyoung.seed.domain.user.dto.request.SignUpRequest;
import com.sayyoung.seed.domain.user.dto.response.UserResponse;
import com.sayyoung.seed.domain.user.service.UserService;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        UserResponse response = userService.signUp(request);

        return ResponseFactory.success(SuccessCode.COMMON_CREATED, response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        TokenResponse response = userService.login(request);

        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @PostMapping("/me/intake")
    public ResponseEntity<ApiResponse<Void>> submitIntake(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody IntakeRequest request
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        userService.submitIntake(userId, request);

        return ResponseFactory.success(SuccessCode.COMMON_CREATED);
    }
}

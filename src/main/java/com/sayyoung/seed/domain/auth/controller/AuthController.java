package com.sayyoung.seed.domain.auth.controller;

import com.sayyoung.seed.domain.auth.dto.request.RefreshTokenRequest;
import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.auth.service.AuthService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 재발급 및 로그아웃 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse response = authService.reissue(request.getRefreshToken());

        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());

        return ResponseFactory.success(SuccessCode.COMMON_OK);
    }
}

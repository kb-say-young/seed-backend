package com.sayyoung.seed.domain.user.controller;

import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisStatusResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisService;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.dto.request.LoginRequest;
import com.sayyoung.seed.domain.user.dto.request.SignUpRequest;
import com.sayyoung.seed.domain.user.dto.response.UserMeResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
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
    private final DiagnosisService diagnosisService;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        UserMeResponse response = userService.getMe(userId);

        return ResponseFactory.success(SuccessCode.COMMON_OK, response);
    }

    @PostMapping("/me/intake")
    public ResponseEntity<ApiResponse<DiagnosisStatusResponse>> submitIntake(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody IntakeRequest request
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        userService.submitIntake(userId, request);

        // 프로필/목표 저장이 커밋된 뒤에 별도로 AI 진단을 수행한다. diagnose() 내부는
        // Dify 호출(느린 외부 HTTP) 구간 동안 DB 트랜잭션을 잡지 않도록 단계별로 독립
        // 커밋하므로, submitIntake()의 @Transactional 안에서 호출하면 안 된다.
        Long diagnosisId = diagnosisService.diagnose(userId, request);

        // 프론트가 어떤 진단 결과를 조회해야 하는지 알 수 있도록 diagnosisId/status를 응답에 담는다.
        DiagnosisStatusResponse response = diagnosisService.getDiagnosis(diagnosisId);

        return ResponseFactory.success(SuccessCode.COMMON_CREATED, response);
    }
}

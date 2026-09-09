package com.sayyoung.seed.domain.user.controller;

import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisStatusResponse;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.dto.request.LoginRequest;
import com.sayyoung.seed.domain.user.dto.request.SignUpRequest;
import com.sayyoung.seed.domain.user.dto.response.UserResponse;
import com.sayyoung.seed.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 사용자 API의 Swagger 명세를 정의합니다.
 */
@Tag(
        name = "사용자 API",
        description = "회원가입 및 로그인 API"
)
public interface UserControllerDocs {

    /**
     * 회원가입 API 명세입니다.
     *
     * @param request 회원가입 요청
     * @return 공통 응답 형식으로 감싼 생성된 사용자 정보
     */
    @Operation(
            summary = "회원가입",
            description = "로그인 아이디만으로 사용자를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 아이디"
            )
    })
    ResponseEntity<ApiResponse<UserResponse>> signUp(
            @Valid SignUpRequest request
    );

    /**
     * 로그인 API 명세입니다.
     *
     * @param request 로그인 요청
     * @return 공통 응답 형식으로 감싼 액세스/리프레시 토큰
     */
    @Operation(
            summary = "로그인",
            description = "로그인 아이디만으로 인증하고 액세스/리프레시 토큰을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 아이디"
            )
    })
    ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid LoginRequest request
    );

    /**
     * 진단 정보(유저 정보/목표) 제출 API 명세입니다.
     *
     * @param userId  액세스 토큰에서 추출된 사용자 식별자
     * @param request 진단 정보 제출 요청
     * @return 생성된 진단의 ID와 상태를 담은 공통 응답
     */
    @Operation(
            summary = "진단 정보 제출",
            description = "로그인 이후 화면에서 입력한 기본 정보/소득·예산/목표를 저장하고 AI 진단까지 수행합니다. " +
                    "재제출 시 기본 정보는 갱신되고 목표는 전체 교체됩니다. 응답의 diagnosisId로 결과를 조회할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "진단 정보 제출 및 AI 진단 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            )
    })
    ResponseEntity<ApiResponse<DiagnosisStatusResponse>> submitIntake(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long userId,
            @Valid IntakeRequest request
    );
}

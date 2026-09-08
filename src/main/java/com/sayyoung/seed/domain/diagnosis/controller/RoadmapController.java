package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로드맵 화면(런웨이 바) 조회 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class RoadmapController {

    private final DiagnosisSummaryService diagnosisSummaryService;

    /**
     * 로그인한 사용자의 가장 최근 진단을 기준으로 로드맵 요약을 조회합니다.
     */
    @GetMapping("/roadmap")
    public ResponseEntity<ApiResponse<MyRoadmapResponse>> getMyRoadmap(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        MyRoadmapResponse response = diagnosisSummaryService.getMyRoadmap(userId);

        return ResponseFactory
                .success(SuccessCode.COMMON_OK, response);
    }
}

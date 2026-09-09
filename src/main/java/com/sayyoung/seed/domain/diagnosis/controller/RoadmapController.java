package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.dto.request.ChecklistItemCompleteRequest;
import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.domain.diagnosis.service.RecommendationService;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final RecommendationService recommendationService;

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

    /**
     * 체크리스트 항목을 완료 처리합니다.
     */
    @PostMapping("/roadmap/checklist-items/{itemId}/complete")
    public ResponseEntity<Void> completeChecklistItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId,
            @RequestBody(required = false) ChecklistItemCompleteRequest request
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        recommendationService.completeChecklistItem(userId, itemId, request);

        return ResponseEntity.noContent().build();
    }

    /**
     * 완료 처리한 체크리스트 항목을 다시 미완료로 되돌립니다.
     */
    @DeleteMapping("/roadmap/checklist-items/{itemId}/complete")
    public ResponseEntity<Void> uncompleteChecklistItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId
    ) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        recommendationService.uncompleteChecklistItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }
}

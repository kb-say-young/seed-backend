package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.dto.request.RecommendationCategory;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationResponse;
import com.sayyoung.seed.domain.diagnosis.service.RecommendationService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 로드맵(추천) 조회 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnoses")
public class RecommendationController implements RecommendationControllerDocs {

    private final RecommendationService recommendationService;

    /**
     * 진단에 연관된 추천(로드맵) 목록을 조회합니다.
     *
     * @param diagnosisId 조회할 진단 식별자
     * @param category    상위 카테고리 필터
     * @return 추천 목록
     */
    @GetMapping("/{diagnosisId}/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @PathVariable Long diagnosisId,
            @RequestParam(required = false) RecommendationCategory category
    ) {
        List<RecommendationResponse> response = recommendationService.getRecommendations(diagnosisId, category);

        return ResponseFactory
                .success(SuccessCode.COMMON_OK, response);
    }
}

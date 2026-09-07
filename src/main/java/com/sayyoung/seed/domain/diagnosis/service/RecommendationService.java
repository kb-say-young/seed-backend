package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.request.RecommendationCategory;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 로드맵(추천) 목록 조회와 관련된 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;

    /**
     * 진단에 연관된 추천(로드맵) 목록을 조회합니다.
     *
     * @param diagnosisId 진단 식별자
     * @param category    상위 카테고리 필터, 전달되지 않으면 전체 조회
     * @return 조회된 추천 목록
     * @throws BusinessException 존재하지 않는 진단 식별자인 경우
     */
    public List<RecommendationResponse> getRecommendations(
            Long diagnosisId,
            RecommendationCategory category
    ) {
        // 진단 존재 여부 확인
        diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        // 카테고리 필터 여부에 따라 추천 목록 조회
        List<Recommendation> recommendations = category == null
                ? recommendationRepository.findByDiagnosisId(diagnosisId)
                : recommendationRepository.findByDiagnosisIdAndCategory_Parent_Name(
                        diagnosisId,
                        category.getTopLevelCategoryName()
                );

        return recommendations.stream()
                .map(RecommendationResponse::from)
                .toList();
    }
}

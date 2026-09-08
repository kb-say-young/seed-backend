package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.request.RecommendationCategory;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationDetailResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationResponse;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 로드맵(추천) 조회와 관련된 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;
    private final ChecklistItemRepository checklistItemRepository;

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

    /**
     * 추천(로드맵) 항목 상세와 하위 체크리스트 항목 목록을 조회합니다.
     *
     * @param userId            조회를 요청한 사용자 식별자
     * @param recommendationId  조회할 추천 항목 식별자
     * @return 로드맵 항목 상세 및 체크리스트 목록
     * @throws BusinessException 존재하지 않는 추천 항목이거나, 다른 사용자의 추천 항목인 경우
     */
    public RecommendationDetailResponse getRecommendationDetail(
            Long userId,
            Long recommendationId
    ) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.RECOMMENDATION_NOT_FOUND));

        // 추천 항목이 속한 진단의 소유자와 요청 사용자가 일치하는지 확인
        if (!recommendation.getDiagnosis().getUser().getId().equals(userId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        List<ChecklistItem> checklistItems = checklistItemRepository
                .findByRecommendationIdOrderByOrderNoAsc(recommendationId);

        return RecommendationDetailResponse.from(recommendation, checklistItems);
    }
}

package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.request.ChecklistItemCompleteRequest;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 로드맵(추천) 조회와 관련된 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private static final String STATUS_DONE = "done";
    private static final String STATUS_REVIEW = "review";
    private static final String STATUS_PROGRESS = "progress";

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

        List<Long> recommendationIds = recommendations.stream()
                .map(Recommendation::getId)
                .toList();

        Map<Long, List<ChecklistItem>> checklistItemsByRecommendationId = checklistItemRepository
                .findByRecommendationIdIn(recommendationIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRecommendation().getId()));

        return recommendations.stream()
                .map(recommendation -> RecommendationResponse.from(
                        recommendation,
                        deriveStatus(checklistItemsByRecommendationId.getOrDefault(recommendation.getId(), List.of()))
                ))
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

        return RecommendationDetailResponse.from(recommendation, checklistItems, deriveStatus(checklistItems));
    }

    /**
     * 하위 체크리스트 항목들의 완료 여부로 추천(로드맵) 항목의 상태를 파생시킵니다.
     * 체크리스트가 없으면 확인 필요(review), 전부 완료되었으면 완료(done), 그 외에는 진행 중(progress)입니다.
     *
     * @param checklistItems 상태를 파생시킬 체크리스트 항목 목록
     * @return 파생된 상태 문자열
     */
    private String deriveStatus(List<ChecklistItem> checklistItems) {
        if (checklistItems.isEmpty()) {
            return STATUS_REVIEW;
        }

        return checklistItems.stream().allMatch(ChecklistItem::isDone)
                ? STATUS_DONE
                : STATUS_PROGRESS;
    }

    /**
     * 체크리스트 항목을 완료 처리합니다.
     *
     * @param userId           완료를 요청한 사용자 식별자
     * @param checklistItemId  완료 처리할 체크리스트 항목 식별자
     * @param request          완료 요청 정보(비용/일자는 계약 형태 호환을 위해서만 받으며 아직 저장하지 않음)
     * @throws BusinessException 존재하지 않는 체크리스트 항목이거나, 다른 사용자 소유인 경우
     */
    @Transactional
    public void completeChecklistItem(
            Long userId,
            Long checklistItemId,
            ChecklistItemCompleteRequest request
    ) {
        ChecklistItem checklistItem = checklistItemRepository.findById(checklistItemId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.CHECKLIST_ITEM_NOT_FOUND));

        // 체크리스트 항목이 속한 추천의 소유자와 요청 사용자가 일치하는지 확인
        if (!checklistItem.getRecommendation().getDiagnosis().getUser().getId().equals(userId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        checklistItem.complete();
    }
}

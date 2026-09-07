package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.ChecklistItemDto;
import com.sayyoung.seed.domain.diagnosis.dto.response.DifyWorkflowResponseDto;
import com.sayyoung.seed.domain.diagnosis.dto.RoadmapItemDto;
import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.ChecklistItemRepository;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.repository.UserGoalRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.utils.DifyResponseParseException;
import com.sayyoung.seed.global.utils.DifyResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dify 로드맵 응답을 파싱해 추천/체크리스트로 저장하고 진단 상태를 갱신합니다.
 * 트리거(어떤 이벤트가 이 서비스를 호출할지)는 별도로 구현되며,
 * {@link #applyRoadmap}은 그 트리거가 호출할 진입점이다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisResultService {

    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final UserGoalRepository userGoalRepository;
    private final CategoryRepository categoryRepository;
    private final DiagnosisFailureRecorder diagnosisFailureRecorder;

    /**
     * Dify 로드맵 응답을 진단 결과로 반영합니다.
     * 파싱, 카테고리/목표 매칭, 저장 중 어떤 예외가 발생하더라도 부분 저장 없이 롤백되고,
     * 진단은 실패(failed) 상태로 남는다.
     *
     * @param diagnosisId 진단 식별자
     * @param rawDifyJson Dify 워크플로우가 반환한 원본 응답 JSON
     */
    @Transactional
    public void applyRoadmap(
            Long diagnosisId,
            String rawDifyJson
    ) {
        try {
            saveRoadmap(diagnosisId, rawDifyJson);
        } catch (RuntimeException e) {
            diagnosisFailureRecorder.markFailed(diagnosisId);
            throw e;
        }
    }

    private void saveRoadmap(
            Long diagnosisId,
            String rawDifyJson
    ) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        DifyWorkflowResponseDto response = parse(rawDifyJson);

        for (RoadmapItemDto item : response.getRoadmapItems()) {
            Recommendation recommendation = saveRecommendation(diagnosis, item);
            saveChecklistItems(recommendation, item.getChecklist());
        }

        diagnosis.complete();
    }

    private DifyWorkflowResponseDto parse(
            String rawDifyJson
    ) {
        try {
            return DifyResponseParser.parse(rawDifyJson);
        } catch (DifyResponseParseException e) {
            throw new BusinessException(DiagnosisErrorCode.INVALID_LLM_RESPONSE);
        }
    }

    private Recommendation saveRecommendation(
            Diagnosis diagnosis,
            RoadmapItemDto item
    ) {
        Category category = categoryRepository.findById(item.getOriginSubCategory())
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.CATEGORY_NOT_FOUND));
        UserGoal goal = userGoalRepository.findByUserAndCategory(diagnosis.getUser(), category)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.GOAL_NOT_FOUND));

        return recommendationRepository.save(Recommendation.create(
                category,
                goal,
                diagnosis,
                item.getItemKey(),
                item.getOrderNo(),
                item.getStartOffset().getValue(),
                item.getStartOffset().getUnit(),
                item.getDuration().getValue(),
                item.getDuration().getUnit(),
                item.getTitle(),
                item.getContent(),
                toBigDecimal(item.getTargetAmount()),
                item.getAmountType(),
                item.getTargetCondition(),
                item.getNextAction(),
                item.getCitation()
        ));
    }

    private void saveChecklistItems(
            Recommendation recommendation,
            List<ChecklistItemDto> checklist
    ) {
        for (int i = 0; i < checklist.size(); i++) {
            ChecklistItemDto checklistItem = checklist.get(i);
            checklistItemRepository.save(ChecklistItem.create(
                    recommendation,
                    checklistItem.getItemKey(),
                    checklistItem.getContent(),
                    (short) (i + 1),
                    toBigDecimal(checklistItem.getEstimatedAmount())
            ));
        }
    }

    private static BigDecimal toBigDecimal(
            Long value
    ) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}

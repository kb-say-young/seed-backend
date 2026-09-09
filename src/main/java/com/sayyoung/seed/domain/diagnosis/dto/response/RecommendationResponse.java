package com.sayyoung.seed.domain.diagnosis.dto.response;

import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 로드맵(추천) 목록 조회 결과를 반환하는 DTO입니다.
 */
@Schema(description = "로드맵(추천) 항목 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationResponse {

    /**
     * 추천 항목 식별자입니다.
     */
    @Schema(description = "추천 항목 식별자", example = "1")
    private final Long recommendationId;

    /**
     * 추천 항목 제목입니다.
     */
    @Schema(description = "추천 항목 제목", example = "월세 적립 계획 수립")
    private final String title;

    /**
     * 추천 항목의 세부 카테고리명입니다.
     */
    @Schema(description = "세부 카테고리명", example = "월세")
    private final String category;

    /**
     * 목표 금액입니다.
     */
    @Schema(description = "목표 금액", example = "1500000")
    private final BigDecimal targetAmount;

    /**
     * 다음으로 수행할 행동입니다.
     */
    @Schema(description = "다음 행동", example = "적립 전용 계좌를 개설하고 자동이체를 등록하세요.")
    private final String nextAction;

    /**
     * 하위 체크리스트 완료 여부로 파생한 상태입니다.
     */
    @Schema(description = "상태 (done: 완료, review: 확인 필요, progress: 진행 중)", example = "progress")
    private final String status;

    /**
     * 완료한 하위 체크리스트 항목 수입니다.
     */
    @Schema(description = "완료한 체크리스트 항목 수", example = "1")
    private final int taskDoneCount;

    /**
     * 전체 하위 체크리스트 항목 수입니다.
     */
    @Schema(description = "전체 체크리스트 항목 수", example = "3")
    private final int taskTotalCount;

    /**
     * Recommendation 엔티티를 로드맵 응답 DTO로 변환합니다.
     *
     * @param recommendation 변환할 추천 엔티티
     * @param checklistItems 하위 체크리스트 항목 목록 (완료·전체 개수 계산용)
     * @param status         하위 체크리스트로부터 파생한 상태
     * @return 변환된 로드맵 응답 DTO
     */
    public static RecommendationResponse from(
            Recommendation recommendation,
            List<ChecklistItem> checklistItems,
            String status
    ) {
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getTitle(),
                recommendation.getCategory().getName(),
                recommendation.getTargetAmount(),
                recommendation.getNextAction(),
                status,
                (int) checklistItems.stream().filter(ChecklistItem::isDone).count(),
                checklistItems.size()
        );
    }
}

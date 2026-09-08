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
 * 로드맵 항목 상세 및 체크리스트 목록 조회 결과를 반환하는 DTO입니다.
 */
@Schema(description = "로드맵 항목 상세(체크리스트 포함) 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationDetailResponse {

    @Schema(description = "추천 항목 식별자", example = "1")
    private final Long recommendationId;

    @Schema(description = "세부 카테고리명", example = "월세")
    private final String category;

    @Schema(description = "추천 항목 제목", example = "월세 적립 계획 수립")
    private final String title;

    @Schema(description = "추천 항목 상세 내용", example = "매월 소득의 일부를 월세 적립 계좌에 자동이체하도록 설정합니다.")
    private final String content;

    @Schema(description = "표시 순서", example = "1")
    private final Integer orderNo;

    @Schema(description = "시작 시점 오프셋 값", example = "0")
    private final Integer startOffsetValue;

    @Schema(description = "시작 시점 오프셋 단위", example = "week")
    private final String startOffsetUnit;

    @Schema(description = "기간 값", example = "3")
    private final Integer durationValue;

    @Schema(description = "기간 단위 (week, month)", example = "month")
    private final String durationUnit;

    @Schema(description = "목표 금액", example = "1500000")
    private final BigDecimal targetAmount;

    @Schema(description = "금액 유형 (saving, expense, income)", example = "saving")
    private final String amountType;

    @Schema(description = "목표 달성 조건")
    private final String targetCondition;

    @Schema(description = "다음 행동 안내", example = "적립 전용 계좌를 개설하고 자동이체를 등록하세요.")
    private final String nextAction;

    @Schema(description = "근거/출처")
    private final String citation;

    @Schema(description = "체크리스트 항목 목록")
    private final List<ChecklistItemResponse> checklistItems;

    /**
     * 하위 체크리스트 완료 여부로 파생한 상태입니다.
     */
    @Schema(description = "상태 (done: 완료, review: 확인 필요, progress: 진행 중)", example = "progress")
    private final String status;

    /**
     * Recommendation 엔티티와 체크리스트 항목 목록을 로드맵 상세 응답 DTO로 변환합니다.
     *
     * @param recommendation 변환할 추천(로드맵) 엔티티
     * @param checklistItems 추천 항목에 속한 체크리스트 항목 목록
     * @param status         하위 체크리스트로부터 파생한 상태
     * @return 변환된 로드맵 상세 응답 DTO
     */
    public static RecommendationDetailResponse from(
            Recommendation recommendation,
            List<ChecklistItem> checklistItems,
            String status
    ) {
        return new RecommendationDetailResponse(
                recommendation.getId(),
                recommendation.getCategory().getName(),
                recommendation.getTitle(),
                recommendation.getContent(),
                recommendation.getOrderNo(),
                recommendation.getStartOffsetValue(),
                recommendation.getStartOffsetUnit(),
                recommendation.getDurationValue(),
                recommendation.getDurationUnit(),
                recommendation.getTargetAmount(),
                recommendation.getAmountType(),
                recommendation.getTargetCondition(),
                recommendation.getNextAction(),
                recommendation.getCitation(),
                checklistItems.stream()
                        .map(ChecklistItemResponse::from)
                        .toList(),
                status
        );
    }
}

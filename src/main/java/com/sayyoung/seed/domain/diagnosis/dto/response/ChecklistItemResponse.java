package com.sayyoung.seed.domain.diagnosis.dto.response;

import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 체크리스트 항목 조회 결과를 반환하는 DTO입니다.
 */
@Schema(description = "체크리스트 항목 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChecklistItemResponse {

    @Schema(description = "체크리스트 항목 식별자", example = "1")
    private final Long id;

    @Schema(description = "체크리스트 항목 키", example = "open_saving_account")
    private final String itemKey;

    @Schema(description = "체크리스트 항목 내용", example = "월세 적립 전용 계좌 개설")
    private final String contents;

    @Schema(description = "표시 순서", example = "1")
    private final Short orderNo;

    @Schema(description = "예상 금액", example = "1000000")
    private final BigDecimal estimatedAmount;

    @Schema(description = "진행 상태 (todo, done, skipped)", example = "todo")
    private final String status;

    @Schema(description = "완료 일시", example = "2026-08-01T10:00:00")
    private final LocalDateTime completedAt;

    /**
     * ChecklistItem 엔티티를 체크리스트 항목 응답 DTO로 변환합니다.
     *
     * @param checklistItem 변환할 체크리스트 항목 엔티티
     * @return 변환된 체크리스트 항목 응답 DTO
     */
    public static ChecklistItemResponse from(
            ChecklistItem checklistItem
    ) {
        return new ChecklistItemResponse(
                checklistItem.getId(),
                checklistItem.getItemKey(),
                checklistItem.getContents(),
                checklistItem.getOrderNo(),
                checklistItem.getEstimatedAmount(),
                checklistItem.getStatus(),
                checklistItem.getCompletedAt()
        );
    }
}

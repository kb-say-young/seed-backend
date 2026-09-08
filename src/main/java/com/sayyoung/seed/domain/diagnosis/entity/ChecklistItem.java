package com.sayyoung.seed.domain.diagnosis.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추천(로드맵) 항목별 실행 체크리스트를 나타내는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "checklist_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChecklistItem {

    private static final String STATUS_TODO = "todo";
    private static final String STATUS_DONE = "done";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_items_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendations_id", nullable = false)
    private Recommendation recommendation;

    @Column(name = "item_key", nullable = false, length = 100)
    private String itemKey;

    @Column(name = "contents", length = 255)
    private String contents;

    @Column(name = "order_no", nullable = false)
    private Short orderNo;

    @Column(name = "estimated_amount", precision = 15, scale = 0)
    private BigDecimal estimatedAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private ChecklistItem(
            Recommendation recommendation,
            String itemKey,
            String contents,
            Short orderNo,
            BigDecimal estimatedAmount,
            String status
    ) {
        this.recommendation = recommendation;
        this.itemKey = itemKey;
        this.contents = contents;
        this.orderNo = orderNo;
        this.estimatedAmount = estimatedAmount;
        this.status = status;
    }

    /**
     * LLM 응답의 체크리스트 항목 하나를 미완료(todo) 상태로 생성합니다.
     */
    public static ChecklistItem create(
            Recommendation recommendation,
            String itemKey,
            String contents,
            Short orderNo,
            BigDecimal estimatedAmount
    ) {
        return new ChecklistItem(recommendation, itemKey, contents, orderNo, estimatedAmount, STATUS_TODO);
    }

    /**
     * 체크리스트 항목을 완료(done) 상태로 전환하고 완료 일시를 현재 시각으로 기록합니다.
     */
    public void complete() {
        this.status = STATUS_DONE;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 체크리스트 항목이 완료(done) 상태인지 확인합니다.
     */
    public boolean isDone() {
        return STATUS_DONE.equals(this.status);
    }
}

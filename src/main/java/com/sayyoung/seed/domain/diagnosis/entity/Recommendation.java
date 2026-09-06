package com.sayyoung.seed.domain.diagnosis.entity;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 사용자 목표별로 LLM이 생성한 추천(로드맵) 항목을 나타내는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "recommendations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendations_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private UserGoal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @Column(name = "item_key", nullable = false, length = 100)
    private String itemKey;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "start_offset_value", nullable = false)
    private Integer startOffsetValue;

    @Column(name = "start_offset_unit", nullable = false, length = 10)
    private String startOffsetUnit;

    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Column(name = "duration_unit", nullable = false, length = 10)
    private String durationUnit;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_amount", precision = 15, scale = 0)
    private BigDecimal targetAmount;

    @Column(name = "amount_type", length = 10)
    private String amountType;

    @Column(name = "target_condition", columnDefinition = "TEXT")
    private String targetCondition;

    @Column(name = "next_action", nullable = false, columnDefinition = "TEXT")
    private String nextAction;

    @Column(name = "citation", columnDefinition = "TEXT")
    private String citation;

    private Recommendation(
            Category category,
            UserGoal goal,
            Diagnosis diagnosis,
            String itemKey,
            Integer orderNo,
            Integer startOffsetValue,
            String startOffsetUnit,
            Integer durationValue,
            String durationUnit,
            String title,
            String content,
            BigDecimal targetAmount,
            String amountType,
            String targetCondition,
            String nextAction,
            String citation
    ) {
        this.category = category;
        this.goal = goal;
        this.diagnosis = diagnosis;
        this.itemKey = itemKey;
        this.orderNo = orderNo;
        this.startOffsetValue = startOffsetValue;
        this.startOffsetUnit = startOffsetUnit;
        this.durationValue = durationValue;
        this.durationUnit = durationUnit;
        this.title = title;
        this.content = content;
        this.targetAmount = targetAmount;
        this.amountType = amountType;
        this.targetCondition = targetCondition;
        this.nextAction = nextAction;
        this.citation = citation;
    }

    /**
     * LLM 응답의 로드맵 항목 하나를 추천 엔티티로 생성합니다.
     */
    public static Recommendation create(
            Category category,
            UserGoal goal,
            Diagnosis diagnosis,
            String itemKey,
            Integer orderNo,
            Integer startOffsetValue,
            String startOffsetUnit,
            Integer durationValue,
            String durationUnit,
            String title,
            String content,
            BigDecimal targetAmount,
            String amountType,
            String targetCondition,
            String nextAction,
            String citation
    ) {
        return new Recommendation(
                category, goal, diagnosis, itemKey, orderNo,
                startOffsetValue, startOffsetUnit, durationValue, durationUnit,
                title, content, targetAmount, amountType, targetCondition, nextAction, citation
        );
    }
}

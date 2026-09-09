package com.sayyoung.seed.domain.savings.entity;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 카테고리별 AI 추천/사용자 조정 예산 배분을 나타내는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "budget_allocations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "ai_ratio", precision = 5, scale = 2)
    private BigDecimal aiRatio;

    @Column(name = "ai_amount", precision = 15, scale = 0)
    private BigDecimal aiAmount;

    @Column(name = "user_ratio", precision = 5, scale = 2)
    private BigDecimal userRatio;

    @Column(name = "user_amount", precision = 15, scale = 0)
    private BigDecimal userAmount;

    private BudgetAllocation(
            User user,
            Category category
    ) {
        this.user = user;
        this.category = category;
        this.aiRatio = BigDecimal.ZERO;
        this.aiAmount = BigDecimal.ZERO;
    }

    /**
     * AI 추천 배분이 없던 카테고리에 사용자가 처음 배분을 설정할 때 새 행을 생성합니다.
     * ai_ratio/ai_amount는 DB 컬럼이 NOT NULL이라 "추천 없음"을 뜻하는 0으로 채우고,
     * 사용자 비율/금액은 이후 {@link #updateAllocation}으로 채웁니다.
     */
    public static BudgetAllocation create(
            User user,
            Category category
    ) {
        return new BudgetAllocation(user, category);
    }

    /**
     * 사용자가 직접 조정한 비율이 있으면 그 값을, 없으면 AI 추천 비율을 사용합니다.
     */
    public BigDecimal resolveRatio() {
        return userRatio != null ? userRatio : aiRatio;
    }

    /**
     * 사용자가 직접 조정한 금액이 있으면 그 값을, 없으면 AI 추천 금액을 목표로 사용합니다.
     */
    public BigDecimal resolveGoalAmount() {
        return userAmount != null ? userAmount : aiAmount;
    }

    /**
     * 사용자가 배분 화면(B1 수정 모드)에서 직접 조정한 비율/금액을 반영합니다.
     */
    public void updateAllocation(
            BigDecimal ratio,
            BigDecimal amount
    ) {
        this.userRatio = ratio;
        this.userAmount = amount;
    }
}

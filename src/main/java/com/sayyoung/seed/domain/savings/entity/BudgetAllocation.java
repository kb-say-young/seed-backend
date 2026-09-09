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
 * 이 이슈(저축 조회/등록 API)에서는 카테고리별 목표 금액(goal)을 읽어오는 용도로만 사용하며,
 * 배분 자체를 관리하는 기능(B1 예산, fund-plan 이슈)은 별도로 다룹니다.
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

    @Column(name = "ai_amount", precision = 15, scale = 0)
    private BigDecimal aiAmount;

    @Column(name = "user_amount", precision = 15, scale = 0)
    private BigDecimal userAmount;

    /**
     * 사용자가 직접 조정한 금액이 있으면 그 값을, 없으면 AI 추천 금액을 목표로 사용합니다.
     */
    public BigDecimal resolveGoalAmount() {
        return userAmount != null ? userAmount : aiAmount;
    }
}

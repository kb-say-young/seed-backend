package com.sayyoung.seed.domain.savings.repository;

import com.sayyoung.seed.domain.savings.entity.BudgetAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 예산 배분(카테고리별 목표 금액) 데이터 접근을 담당합니다.
 */
public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocation, Long> {

    /**
     * 특정 사용자의 카테고리별 예산 배분을 모두 조회합니다.
     *
     * @param userId 사용자 식별자
     */
    List<BudgetAllocation> findByUserId(
            Long userId
    );
}

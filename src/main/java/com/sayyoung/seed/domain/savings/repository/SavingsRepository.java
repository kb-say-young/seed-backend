package com.sayyoung.seed.domain.savings.repository;

import com.sayyoung.seed.domain.savings.entity.Savings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 저축 내역 데이터 접근을 담당합니다.
 */
public interface SavingsRepository extends JpaRepository<Savings, Long> {
}

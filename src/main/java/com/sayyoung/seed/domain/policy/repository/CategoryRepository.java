package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 카테고리 데이터 접근을 담당합니다.
 */
public interface CategoryRepository extends JpaRepository<Category, String> {

}

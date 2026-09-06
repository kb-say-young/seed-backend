package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 카테고리 데이터 접근을 담당합니다.
 */
public interface CategoryRepository extends JpaRepository<Category, String> {

    /**
     * 카테고리명으로 카테고리를 조회합니다.
     *
     * @param name 카테고리명
     */
    Optional<Category> findByName(
            String name
    );
}

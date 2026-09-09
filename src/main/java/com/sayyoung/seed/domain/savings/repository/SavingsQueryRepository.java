package com.sayyoung.seed.domain.savings.repository;

import com.sayyoung.seed.domain.savings.entity.Savings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * QueryDSL 기반 저축 내역 조회 기능을 제공합니다.
 * housing/work 카테고리는 categories 테이블의 리프(하위) 카테고리 또는 루트 카테고리 자기 자신을
 * 모두 참조할 수 있으므로, 자기 자신 또는 부모가 루트 카테고리와 일치하는 행을 함께 매칭합니다.
 */
public interface SavingsQueryRepository {

    /**
     * 특정 사용자의 루트 카테고리(housing|work)에 속하는 저축 내역을 전부 조회합니다.
     * 총계/월별 추이 계산에 사용합니다.
     */
    List<Savings> findAllByUserIdAndRootCategoryId(
            Long userId,
            String rootCategoryId
    );

    /**
     * 특정 사용자의 루트 카테고리(housing|work)에 속하는 저축 내역을 페이지 단위로 조회합니다.
     */
    Page<Savings> findPageByUserIdAndRootCategoryId(
            Long userId,
            String rootCategoryId,
            Pageable pageable
    );
}

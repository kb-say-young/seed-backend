package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 체크리스트 항목 데이터 접근을 담당합니다.
 */
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

    /**
     * 특정 추천 항목에 속한 체크리스트 항목을 모두 조회합니다.
     *
     * @param recommendationId 추천 항목 식별자
     */
    List<ChecklistItem> findByRecommendationId(
            Long recommendationId
    );

    /**
     * 특정 추천 항목에 속한 체크리스트 항목을 표시 순서대로 조회합니다.
     *
     * @param recommendationId 추천 항목 식별자
     */
    List<ChecklistItem> findByRecommendationIdOrderByOrderNoAsc(
            Long recommendationId
    );

    /**
     * 여러 추천 항목에 속한 체크리스트 항목을 한 번에 조회합니다.
     *
     * @param recommendationIds 추천 항목 식별자 목록
     */
    List<ChecklistItem> findByRecommendationIdIn(
            List<Long> recommendationIds
    );
}

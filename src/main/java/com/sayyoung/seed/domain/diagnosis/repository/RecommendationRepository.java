package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 추천(로드맵) 항목 데이터 접근을 담당합니다.
 */
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

}

package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 추천(로드맵) 항목 데이터 접근을 담당합니다.
 */
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    /**
     * 특정 진단에서 생성된 추천 항목을 모두 조회합니다.
     *
     * @param diagnosisId 진단 식별자
     */
    List<Recommendation> findByDiagnosisId(
            Long diagnosisId
    );
}

package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 진단 데이터 접근을 담당합니다.
 */
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

}

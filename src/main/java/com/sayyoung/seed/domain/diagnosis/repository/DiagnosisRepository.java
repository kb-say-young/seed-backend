package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 진단 데이터 접근을 담당합니다.
 */
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    /**
     * 사용자가 가장 최근에 생성한 진단을 조회합니다.
     *
     * @param user 조회할 사용자
     */
    Optional<Diagnosis> findFirstByUserOrderByCreatedAtDesc(
            User user
    );
}

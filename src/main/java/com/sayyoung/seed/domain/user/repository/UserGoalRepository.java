package com.sayyoung.seed.domain.user.repository;

import com.sayyoung.seed.domain.user.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 목표 데이터 접근을 담당합니다.
 */
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    /**
     * 사용자의 기존 목표를 모두 삭제합니다. 목표는 재제출 시 전체 교체됩니다.
     *
     * @param userId 사용자 식별자
     */
    void deleteAllByUserId(
            Long userId
    );
}

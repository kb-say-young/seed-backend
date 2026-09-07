package com.sayyoung.seed.domain.user.repository;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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

    /**
     * 특정 사용자가 특정 카테고리로 설정한 목표를 조회합니다.
     *
     * @param user     목표를 설정한 사용자
     * @param category 목표의 세부 카테고리
     */
    Optional<UserGoal> findByUserAndCategory(
            User user,
            Category category
    );
}

package com.sayyoung.seed.domain.user.repository;

import com.sayyoung.seed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 데이터 접근을 담당합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 로그인 아이디로 사용자를 조회합니다.
     *
     * @param loginId 조회할 로그인 아이디
     * @return 조회된 사용자
     */
    Optional<User> findByLoginId(
            String loginId
    );

    /**
     * 로그인 아이디가 이미 사용 중인지 확인합니다.
     *
     * @param loginId 확인할 로그인 아이디
     * @return 이미 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByLoginId(
            String loginId
    );
}

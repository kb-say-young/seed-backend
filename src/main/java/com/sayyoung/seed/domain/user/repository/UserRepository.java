package com.sayyoung.seed.domain.user.repository;

import com.sayyoung.seed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 데이터 접근을 담당합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

}

package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Policy 기본 CRUD와 QueryDSL 정책 조회 기능을 제공한다.
 */
public interface PolicyRepository extends JpaRepository<Policy, Long>, PolicyQueryRepository {
}

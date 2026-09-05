package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    // 정책 식별번호로 정책을 조회한다.
    Optional<Policy> findByPolicyNo(
            String policyNo
    );
}

package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * QueryDSL 기반 정책 조회 기능을 제공한다.
 */
public interface PolicyQueryRepository {

    /**
     * 조건에 맞는 정책을 조회한다.
     */
    Page<PolicyMatchResult> findMatchedPolicies(
            PolicyFilterCondition condition,
            Pageable pageable
    );
}

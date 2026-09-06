package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;

import java.util.List;

/**
 * QueryDSL을 이용한 정책 동적 조회를 담당하는 Repository.
 */
public interface PolicyQueryRepository {

    // 하나의 Goal 조건에 맞는 정책 목록을 조회한다.
    List<PolicyMatchResult> findByCondition(
            PolicyFilterCondition condition
    );
}

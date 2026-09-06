package com.sayyoung.seed.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * QueryDSL 정책 필터링 결과를 담는 내부 객체.
 */
@Getter
@AllArgsConstructor
public class PolicyMatchResult {

    // Goal-Policy 관계 저장에 사용할 내부 PK
    private Long policyId;

    // 온통청년 정책번호
    private String policyNo;

    // 정책명
    private String name;

    // 정책 설명
    private String description;

    // 운영 기관명
    private String institutionName;
}

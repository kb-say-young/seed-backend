package com.sayyoung.seed.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * QueryDSL 정책 필터링 결과를 담는 내부 객체.
 */
@Getter
@AllArgsConstructor
public class PolicyMatchResult {

    // 정책 ID
    private Long id;

    // 정책명
    private String name;

    // 정책 설명
    private String description;

    // 운영 기관명
    private String institutionName;

    // 신청 시작일
    private LocalDate applyStartDate;

    // 신청 종료일
    private LocalDate applyEndDate;

    // 자립준비청년 대상 여부
    private boolean independentYouth;
}

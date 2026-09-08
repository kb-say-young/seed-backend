package com.sayyoung.seed.domain.policy.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 정책 검색 조건을 담는 내부 객체.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PolicyFilterCondition {

    // Goal의 세부 카테고리 ID
    private String categoryId;

    // 정책 조회에 사용할 지역 코드
    private String regionCode;

    // 사용자 만 나이
    private Integer age;

    // 사용자 월 평균 소득 (원 단위)
    private Long income;

    // 취업 상태 코드
    private String jobCode;

    // 학력 코드
    private String schoolCode;

    // 특화 대상 코드
    private String targetCode;

    // 정책 신청 기간 판단 기준일
    private LocalDate currentDate;
}

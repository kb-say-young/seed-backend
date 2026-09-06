package com.sayyoung.seed.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 하나의 Goal을 기준으로 정책을 조회할 때 사용하는 내부 조건 객체.
 * 프론트 요청 전체가 아니라 정책 필터링에 필요한 값만 담는다.
 */
@Getter
@AllArgsConstructor
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

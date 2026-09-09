package com.sayyoung.seed.domain.policy.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sayyoung.seed.domain.policy.entity.Policy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 정책 상세 조회 응답 DTO입니다.
 */
@Getter
@JsonPropertyOrder({
        "id",
        "policyNo",
        "name",
        "description",
        "supportContent",
        "institutionName",
        "businessStartDate",
        "businessEndDate",
        "applicationMethod",
        "applicationUrl",
        "requiredDocuments",
        "minAge",
        "maxAge",
        "incomeCondition",
        "qualification",
        "restriction",
        "independentYouth"
})
public class PolicyDetailResponseDto {

    // 온통청년 Open API 날짜 형식
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 우리 서비스 내부 정책 ID
    private final Long id;

    // 온통청년 정책번호
    private final String policyNo;

    // 정책명
    private final String name;

    // 정책 설명
    private final String description;

    // 정책 지원 내용
    private final String supportContent;

    // 주관기관명
    private final String institutionName;

    // 사업 시작일
    private final LocalDate businessStartDate;

    // 사업 종료일
    private final LocalDate businessEndDate;

    // 신청 방법
    private final String applicationMethod;

    // 신청 URL
    private final String applicationUrl;

    // 제출 서류
    private final String requiredDocuments;

    // 지원 대상 최소 연령
    private final Integer minAge;

    // 지원 대상 최대 연령
    private final Integer maxAge;

    // 소득 조건
    private final String incomeCondition;

    // 추가 신청 자격
    private final String qualification;

    // 참여 제한 대상
    private final String restriction;

    // 자립준비청년 관련 정책 여부
    private final boolean independentYouth;

    /**
     * 정책 상세 응답 객체를 생성합니다.
     */
    @Builder(access = AccessLevel.PRIVATE)
    private PolicyDetailResponseDto(
            Long id,
            String policyNo,
            String name,
            String description,
            String supportContent,
            String institutionName,
            LocalDate businessStartDate,
            LocalDate businessEndDate,
            String applicationMethod,
            String applicationUrl,
            String requiredDocuments,
            Integer minAge,
            Integer maxAge,
            String incomeCondition,
            String qualification,
            String restriction,
            boolean independentYouth
    ) {
        this.id = id;
        this.policyNo = policyNo;
        this.name = name;
        this.description = description;
        this.supportContent = supportContent;
        this.institutionName = institutionName;
        this.businessStartDate = businessStartDate;
        this.businessEndDate = businessEndDate;
        this.applicationMethod = applicationMethod;
        this.applicationUrl = applicationUrl;
        this.requiredDocuments = requiredDocuments;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.incomeCondition = incomeCondition;
        this.qualification = qualification;
        this.restriction = restriction;
        this.independentYouth = independentYouth;
    }

    /**
     * 내부 정책 정보와 외부 정책 상세 정보를 조합합니다.
     */
    public static PolicyDetailResponseDto from(
            Policy policy,
            YouthPolicyApiDetailResponseDto.YouthPolicyDetailItem detail
    ) {
        return PolicyDetailResponseDto.builder()
                .id(policy.getId())
                .policyNo(detail.getPlcyNo())
                .name(detail.getPlcyNm())
                .description(
                        toNullable(detail.getPlcyExplnCn())
                )
                .supportContent(
                        toNullable(detail.getPlcySprtCn())
                )
                .institutionName(
                        toNullable(detail.getSprvsnInstCdNm())
                )
                .businessStartDate(
                        toLocalDate(detail.getBizPrdBgngYmd())
                )
                .businessEndDate(
                        toLocalDate(detail.getBizPrdEndYmd())
                )
                .applicationMethod(
                        toNullable(detail.getPlcyAplyMthdCn())
                )
                .applicationUrl(
                        toNullable(detail.getAplyUrlAddr())
                )
                .requiredDocuments(
                        toNullable(detail.getSbmsnDcmntCn())
                )
                .minAge(
                        toInteger(detail.getSprtTrgtMinAge())
                )
                .maxAge(
                        toInteger(detail.getSprtTrgtMaxAge())
                )
                .incomeCondition(
                        toNullable(detail.getEarnEtcCn())
                )
                .qualification(
                        toNullable(detail.getAddAplyQlfcCndCn())
                )
                .restriction(
                        toNullable(detail.getPtcpPrpTrgtCn())
                )
                .independentYouth(
                        policy.isIndependentYouth()
                )
                .build();
    }

    /**
     * 빈 문자열 또는 공백을 null로 변환합니다.
     */
    private static String toNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    /**
     * yyyyMMdd 형식 문자열을 LocalDate로 변환합니다.
     */
    private static LocalDate toLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(
                value.trim(),
                DATE_FORMATTER
        );
    }

    /**
     * 숫자 문자열을 Integer로 변환합니다.
     */
    private static Integer toInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value.trim());
    }
}
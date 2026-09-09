package com.sayyoung.seed.domain.policy.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "institutionName",
        "applyStartDate",
        "applyEndDate",
        "independentYouth"
})
public class PolicyRecommendationResponseDto {

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

    public static PolicyRecommendationResponseDto from(PolicyMatchResult result) {
        return new PolicyRecommendationResponseDto(
                result.getId(),
                result.getName(),
                result.getDescription(),
                result.getInstitutionName(),
                result.getApplyStartDate(),
                result.getApplyEndDate(),
                result.isIndependentYouth()
        );
    }
}

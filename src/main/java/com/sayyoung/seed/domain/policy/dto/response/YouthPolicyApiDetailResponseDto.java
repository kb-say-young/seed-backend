package com.sayyoung.seed.domain.policy.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 청년정책 Open API 상세 조회 응답 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class YouthPolicyApiDetailResponseDto {

    // Open API 응답 코드
    private int resultCode;

    // Open API 응답 메시지
    private String resultMessage;

    // 정책 상세 조회 결과
    private Result result;

    /**
     * 정책 상세 조회 결과를 담습니다.
     */
    @Getter
    @NoArgsConstructor
    public static class Result {

        // 조회된 정책 목록
        private List<YouthPolicyDetailItem> youthPolicyList;
    }

    /**
     * 청년정책 상세 정보를 담습니다.
     */
    @Getter
    @NoArgsConstructor
    public static class YouthPolicyDetailItem {

        // 정책번호
        private String plcyNo;

        // 정책명
        private String plcyNm;

        // 정책 설명
        private String plcyExplnCn;

        // 정책 지원 내용
        private String plcySprtCn;

        // 주관기관명
        private String sprvsnInstCdNm;

        // 사업 시작일
        private String bizPrdBgngYmd;

        // 사업 종료일
        private String bizPrdEndYmd;

        // 신청 방법
        private String plcyAplyMthdCn;

        // 신청 URL
        private String aplyUrlAddr;

        // 제출 서류
        private String sbmsnDcmntCn;

        // 지원 대상 최소 연령
        private String sprtTrgtMinAge;

        // 지원 대상 최대 연령
        private String sprtTrgtMaxAge;

        // 소득 조건
        private String earnEtcCn;

        // 추가 신청 자격
        private String addAplyQlfcCndCn;

        // 참여 제한 대상
        private String ptcpPrpTrgtCn;
    }
}
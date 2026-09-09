package com.sayyoung.seed.domain.user.dto.response;

import com.sayyoung.seed.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 내 정보 조회 응답 DTO입니다.
 */
@Schema(description = "내 정보 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMeResponse {

    @Schema(description = "사용자 식별자", example = "1")
    private final Long id;

    @Schema(description = "로그인 아이디", example = "seedyouth")
    private final String loginId;

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "생년월일", example = "2000-01-01")
    private final LocalDate birthDate;

    @Schema(description = "휴대폰 번호(하이픈 제외)", example = "01012345678")
    private final String phone;

    @Schema(description = "진단 프로필. 진단 정보 제출(intake) 전이면 null")
    private final Profile profile;

    /**
     * User 엔티티를 내 정보 조회 응답 DTO로 변환합니다.
     *
     * @param user    변환할 사용자 엔티티
     * @param profile 진단 프로필. intake 전이면 null
     * @return 변환된 내 정보 조회 응답 DTO
     */
    public static UserMeResponse of(
            User user,
            Profile profile
    ) {
        return new UserMeResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getBirthDate(),
                user.getPhoneNumber(),
                profile
        );
    }

    @Schema(description = "진단 프로필")
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Profile {

        @Schema(description = "보호 종료 예정일")
        private final LocalDate protectionEndDate;

        @Schema(description = "청년 자립 지원 대상 여부")
        private final Boolean isYouthSupport;

        @Schema(description = "기초생활수급자 여부")
        private final Boolean isBasicRecipient;

        @Schema(description = "지역 코드(시/군/구)", example = "11110")
        private final String regionCode;

        @Schema(description = "지역 표시명(시/도 + 시/군/구)", example = "서울특별시 종로구")
        private final String regionDisplay;

        @Schema(description = "학력. 온보딩 드롭다운 문자열 그대로(코드 매핑 없음)", example = "고교졸업")
        private final String education;

        @Schema(description = "소득")
        private final Long income;

        @Schema(description = "가구원 수")
        private final Short householdSize;

        @Schema(description = "디딤씨앗통장(CDA) 잔액")
        private final BigDecimal fixedBudget;

        @Schema(description = "배분 가능한 예산")
        private final BigDecimal budget;

        @Schema(description = "디딤씨앗통장(CDA) 보유 여부")
        private final Boolean hasCda;

        /**
         * User 엔티티와 계산된 지역 표시명으로 진단 프로필 DTO를 생성합니다.
         *
         * @param user          변환할 사용자 엔티티
         * @param regionDisplay regionCode로부터 계산된 지역 표시명
         * @return 생성된 진단 프로필 DTO
         */
        public static Profile of(
                User user,
                String regionDisplay
        ) {
            return new Profile(
                    user.getProtectionEndDate(),
                    user.getYouthSupport(),
                    user.getBasicRecipient(),
                    user.getRegionCode(),
                    regionDisplay,
                    user.getEducation(),
                    user.getIncome(),
                    user.getHouseholdSize(),
                    user.getFixedBudget(),
                    user.getBudget(),
                    user.getHasCda()
            );
        }
    }
}

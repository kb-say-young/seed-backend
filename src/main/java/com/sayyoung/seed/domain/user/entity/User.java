package com.sayyoung.seed.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 사용자 정보를 나타내는 엔티티입니다.
 * 진단 프로필 컬럼(protection_end_date 이하)은 `카테고리별_요청_API_계약서.md` §3.1 기준으로
 * users 테이블 컬럼명을 그대로 따른다.
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 30)
    private String loginId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "birth_date", nullable = false, length = 8)
    private String birthDate;

    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "protection_end_date")
    private LocalDate protectionEndDate;

    @Column(name = "is_youth_support")
    private Boolean youthSupport;

    @Column(name = "fixed_budget", precision = 15, scale = 0)
    private BigDecimal fixedBudget;

    @Column(name = "region_code", length = 5)
    private String regionCode;

    @Column(name = "income")
    private Long income;

    @Column(name = "is_basic_recipient")
    private Boolean basicRecipient;

    @Column(name = "household_size")
    private Short householdSize;

    @Column(name = "budget", precision = 10, scale = 0)
    private BigDecimal budget;

    @Column(name = "has_cda")
    private Boolean hasCda;

    private User(
            String loginId,
            String name,
            String birthDate,
            String phoneNumber
    ) {
        this.loginId = loginId;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 회원가입 시 입력받은 정보로 사용자를 생성합니다.
     *
     * @param loginId     로그인 아이디
     * @param name        이름
     * @param birthDate   생년월일(yyyyMMdd)
     * @param phoneNumber 휴대폰 번호(하이픈 제외)
     * @return 생성된 사용자 엔티티
     */
    public static User create(
            String loginId,
            String name,
            String birthDate,
            String phoneNumber
    ) {
        return new User(loginId, name, birthDate, phoneNumber);
    }

    /**
     * 진단(공통 프로필) 정보를 갱신합니다. 재제출 시 덮어씁니다.
     */
    public void updateProfile(
            LocalDate protectionEndDate,
            Boolean youthSupport,
            BigDecimal fixedBudget,
            String regionCode,
            Long income,
            Boolean basicRecipient,
            Short householdSize
    ) {
        this.protectionEndDate = protectionEndDate;
        this.youthSupport = youthSupport;
        this.fixedBudget = fixedBudget;
        this.regionCode = regionCode;
        this.income = income;
        this.basicRecipient = basicRecipient;
        this.householdSize = householdSize;
    }
}

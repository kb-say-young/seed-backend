package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PolicyRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Test
    void 세부_카테고리로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("12")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 카테고리와_지역으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("12")
                .regionCode("11530")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                                + " / "
                                + policy.getInstitutionName()
                )
        );
    }

    @Test
    void 카테고리_지역_연령으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("12")
                .regionCode("11530")
                .age(24)
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 카테고리_지역_연령_소득으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("12")
                .regionCode("11530")
                .age(24)
                .income(1_500_000L)
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 직업_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("21")
                .jobCode("0013006")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 직업_조건은_제한없음_또는_사용자조건과_일치해야한다() {

        // given
        String userJobCode = "0013010";

        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("21")
                .jobCode(userJobCode)
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void 학력_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("21")
                .schoolCode("0049010")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 특화대상_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("21")
                .targetCode("0014004")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 신청기간_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("41")
                .regionCode("11530")
                .age(24)
                .income(1_500_000L)
                .jobCode("0013010")
                .schoolCode("0049005")
                .targetCode("0014010")
                .currentDate(LocalDate.of(2026, 9, 6))
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                                + " / "
                                + policy.getApplyStartDate()
                                + " ~ "
                                + policy.getApplyEndDate()
                )
        );
    }

    @Test
    void 모든_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId("21")
                .regionCode("11530")
                .age(24)
                .income(1_500_000L)
                .jobCode("사용자_직업코드")
                .schoolCode("사용자_학력코드")
                .targetCode("사용자_특화대상코드")
                .currentDate(LocalDate.now())
                .build();

        // when
        List<PolicyMatchResult> result =
                policyRepository.findMatchedPolicies(condition);

        // then
        assertThat(result).isNotNull();

        result.forEach(policy ->
                System.out.println(
                        policy.getId()
                                + " / "
                                + policy.getName()
                                + " / "
                                + policy.isIndependentYouth()
                )
        );
    }
}
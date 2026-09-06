package com.sayyoung.seed.domain.policy.repository;

import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
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
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "12",
                null,
                null,
                null,
                null,
                null,
                null,
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 카테고리와_지역으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "12",
                "11530",
                null,
                null,
                null,
                null,
                null,
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getPolicyNo()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 카테고리_지역_연령으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "12",
                "11530",
                24,
                null,
                null,
                null,
                null,
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 카테고리_지역_연령_소득으로_정책을_조회한다() {

        PolicyFilterCondition condition = new PolicyFilterCondition(
                "12",
                "11530",
                24,
                1_500_000L,
                null,
                null,
                null,
                LocalDate.now()
        );

        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 직업_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "21",
                null,
                null,
                null,
                "0013006",
                null,
                null,
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 직업_조건은_제한없음_또는_사용자조건과_일치해야한다() {

        // given
        String userJobCode = "0013010";

        PolicyFilterCondition condition =
                new PolicyFilterCondition(
                        "21",
                        null,
                        null,
                        null,
                        userJobCode,
                        null,
                        null,
                        LocalDate.now()
                );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void 학력_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "21",
                null,
                null,
                null,
                null,
                "0049010",
                null,
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 특화대상_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "21",
                null,
                null,
                null,
                null,
                null,
                "0014004",
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 신청기간_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "41",
                "11530",
                24,
                1_500_000L,
                "0013010",
                "0049005",
                "0014010",
                LocalDate.of(2026, 9, 6)
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotEmpty();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getName()
                )
        );
    }

    @Test
    void 모든_조건으로_정책을_조회한다() {

        // given
        PolicyFilterCondition condition = new PolicyFilterCondition(
                "21",
                "11530",
                24,
                1_500_000L,
                "사용자_직업코드",
                "사용자_학력코드",
                "사용자_특화대상코드",
                LocalDate.now()
        );

        // when
        List<PolicyMatchResult> result = policyRepository.findByCondition(condition);

        // then
        assertThat(result).isNotNull();

        result.forEach(policy ->
                System.out.println(
                        policy.getPolicyId()
                                + " / "
                                + policy.getPolicyNo()
                                + " / "
                                + policy.getName()
                )
        );
    }
}
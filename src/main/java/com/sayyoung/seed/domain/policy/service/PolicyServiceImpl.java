package com.sayyoung.seed.domain.policy.service;

import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import com.sayyoung.seed.domain.policy.repository.PolicyRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.exception.UserErrorCode;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * 정책 조회 비즈니스 로직을 처리한다.
 */
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    // 사용자 정보 조회
    private final UserRepository userRepository;

    // QueryDSL 기반 정책 조회
    private final PolicyRepository policyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PolicyRecommendationResponseDto> getRecommendations(
            Long userId,
            String categoryId
    ) {
        // 현재 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
        );

        // 조회 기준 날짜
        LocalDate currentDate = LocalDate.now();

        // 만 나이 계산
        int calculatedAge = calculateAge(
                user.getBirthDate(),
                currentDate
        );

        // 맞춤 정책 필터 조건 생성
        PolicyFilterCondition condition = PolicyFilterCondition.builder()
                .categoryId(categoryId)
                .regionCode(user.getRegionCode())
                .income(user.getIncome())
                .currentDate(currentDate)
                .age(calculatedAge)
                .build();

        // QueryDSL 기반 맞춤 정책 조회
        List<PolicyMatchResult> policies = policyRepository.findMatchedPolicies(condition);

        return policies
                .stream()
                .map(PolicyRecommendationResponseDto::from)
                .toList();
    }

    /**
     * 기준일을 바탕으로 만 나이를 계산한다.
     */
    private int calculateAge(
            LocalDate birthDate,
            LocalDate currentDate
    ) {
        return Period.between(
                birthDate,
                currentDate
        ).getYears();
    }
}

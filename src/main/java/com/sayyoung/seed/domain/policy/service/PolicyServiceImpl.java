package com.sayyoung.seed.domain.policy.service;

import com.sayyoung.seed.domain.policy.client.YouthPolicyClient;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.response.PolicyDetailResponseDto;
import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import com.sayyoung.seed.domain.policy.dto.response.YouthPolicyApiDetailResponseDto;
import com.sayyoung.seed.domain.policy.entity.Policy;
import com.sayyoung.seed.domain.policy.exception.PolicyErrorCode;
import com.sayyoung.seed.domain.policy.repository.PolicyRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.exception.UserErrorCode;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * 정책 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    // 사용자 정보 조회
    private final UserRepository userRepository;

    // QueryDSL 기반 정책 조회
    private final PolicyRepository policyRepository;

    private final YouthPolicyClient youthPolicyClient;

    /**
     * 사용자 조건에 맞는 정책을 페이지 단위로 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PolicyRecommendationResponseDto> getRecommendations(
            Long userId,
            String categoryId,
            int page,
            int size
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

        // 페이지 요청 정보 생성
        Pageable pageable = PageRequest.of(
                page,
                size
        );

        // QueryDSL 기반 맞춤 정책 조회
        Page<PolicyMatchResult> policies =
                policyRepository.findMatchedPolicies(
                        condition,
                        pageable
                );

        // 내부 조회 결과를 API 응답 DTO로 변환
        return policies.map(
                PolicyRecommendationResponseDto::from
        );
    }

    /**
     * 정책 ID를 기준으로 상세 정보를 조회합니다.
     */
    @Override
    public PolicyDetailResponseDto getPolicyDetail(
            Long policyId
    ) {
        // 우리 DB에서 정책 조회
        Policy policy = policyRepository.findById(policyId).orElseThrow(
                () -> new BusinessException(PolicyErrorCode.POLICY_NOT_FOUND)
        );

        // DB에 저장된 외부 정책번호로 Open API 호출
        YouthPolicyApiDetailResponseDto apiResponse = youthPolicyClient.getPolicyDetail(
                policy.getPolicyNo()
        );

        // 외부 API 응답에서 정책 상세 정보 추출
        YouthPolicyApiDetailResponseDto.YouthPolicyDetailItem detail = extractPolicyDetail(apiResponse);

        // 내부 정책 정보와 외부 상세 정보를 조합
        return PolicyDetailResponseDto.from(
                policy,
                detail
        );
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

    /**
     * 외부 API 응답에서 단건 정책 상세 정보를 추출합니다.
     */
    private YouthPolicyApiDetailResponseDto.YouthPolicyDetailItem extractPolicyDetail(
            YouthPolicyApiDetailResponseDto response
    ) {
        // 응답 구조가 올바르지 않은 경우
        if (response == null
                || response.getResult() == null
                || response.getResult().getYouthPolicyList() == null
        ) {
            throw new BusinessException(PolicyErrorCode.POLICY_DETAIL_NOT_FOUND);
        }

        // 상세조회 결과 중 첫 번째 정책 반환
        return response.getResult()
                .getYouthPolicyList()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException(PolicyErrorCode.POLICY_DETAIL_NOT_FOUND)
                );
    }
}
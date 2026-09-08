package com.sayyoung.seed.domain.policy.service;

import static org.junit.jupiter.api.Assertions.*;

import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.response.PolicyRecommendationResponseDto;
import com.sayyoung.seed.domain.policy.repository.PolicyRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PolicyService의 맞춤 정책 조회 기능을 테스트한다.
 */
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    // 사용자 조회 Repository
    @Mock
    private UserRepository userRepository;

    // 정책 조회 Repository
    @Mock
    private PolicyRepository policyRepository;

    // 테스트 대상 서비스
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        policyService = new PolicyServiceImpl(
                userRepository,
                policyRepository
        );
    }

    @Test
    void 사용자_정보와_카테고리로_맞춤_정책을_조회한다() {

        // given
        Long userId = 1L;
        String categoryId = "12";

        // 테스트용 사용자 정보
        User user = mock(User.class);

        when(user.getBirthDate())
                .thenReturn(LocalDate.of(2001, 5, 10));

        when(user.getRegionCode())
                .thenReturn("11530");

        when(user.getIncome())
                .thenReturn(1_500_000L);

        // QueryDSL 조회 결과
        PolicyMatchResult policy = new PolicyMatchResult(
                1L,
                "청년 월세 지원",
                "청년의 주거비 부담을 완화하기 위한 정책입니다.",
                "서울특별시",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                true
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(policyRepository.findMatchedPolicies(any()))
                .thenReturn(List.of(policy));

        // when
        List<PolicyRecommendationResponseDto> result =
                policyService.getRecommendations(
                        userId,
                        categoryId
                );

        // then
        assertThat(result).hasSize(1);

        PolicyRecommendationResponseDto response = result.get(0);

        assertThat(response.getId())
                .isEqualTo(1L);

        assertThat(response.getName())
                .isEqualTo("청년 월세 지원");

        assertThat(response.getDescription())
                .isEqualTo("청년의 주거비 부담을 완화하기 위한 정책입니다.");

        assertThat(response.getInstitutionName())
                .isEqualTo("서울특별시");

        assertThat(response.getApplyStartDate())
                .isEqualTo(LocalDate.of(2026, 1, 1));

        assertThat(response.getApplyEndDate())
                .isEqualTo(LocalDate.of(2026, 12, 31));

        assertThat(response.isIndependentYouth())
                .isTrue();
    }

    @Test
    void 사용자_정보를_정책_필터_조건으로_변환한다() {

        // given
        Long userId = 1L;
        String categoryId = "12";

        // 테스트용 사용자 정보
        User user = mock(User.class);

        when(user.getBirthDate())
                .thenReturn(LocalDate.of(2001, 5, 10));

        when(user.getRegionCode())
                .thenReturn("11530");

        when(user.getIncome())
                .thenReturn(1_500_000L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(policyRepository.findMatchedPolicies(any()))
                .thenReturn(List.of());

        // Repository에 전달되는 조건 캡처
        ArgumentCaptor<PolicyFilterCondition> captor =
                ArgumentCaptor.forClass(
                        PolicyFilterCondition.class
                );

        // when
        policyService.getRecommendations(
                userId,
                categoryId
        );

        // then
        verify(policyRepository)
                .findMatchedPolicies(
                        captor.capture()
                );

        PolicyFilterCondition condition =
                captor.getValue();

        assertThat(condition.getCategoryId())
                .isEqualTo("12");

        assertThat(condition.getRegionCode())
                .isEqualTo("11530");

        assertThat(condition.getIncome())
                .isEqualTo(1_500_000L);

        assertThat(condition.getAge())
                .isEqualTo(
                        calculateExpectedAge(
                                LocalDate.of(2001, 5, 10)
                        )
                );

        assertThat(condition.getCurrentDate())
                .isEqualTo(LocalDate.now());
    }

    @Test
    void 존재하지_않는_사용자는_예외가_발생한다() {

        // given
        Long userId = 999L;
        String categoryId = "12";

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> policyService.getRecommendations(
                        userId,
                        categoryId
                )
        ).isInstanceOf(BusinessException.class);

        // 사용자가 없으면 정책 조회를 수행하지 않는다.
        verify(policyRepository, never())
                .findMatchedPolicies(any());
    }

    /**
     * 테스트 기준 만 나이를 계산한다.
     */
    private int calculateExpectedAge(
            LocalDate birthDate
    ) {
        return java.time.Period.between(
                birthDate,
                LocalDate.now()
        ).getYears();
    }
}
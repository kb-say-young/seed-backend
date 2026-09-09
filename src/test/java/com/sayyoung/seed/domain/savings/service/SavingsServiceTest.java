package com.sayyoung.seed.domain.savings.service;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.savings.dto.request.SavingsCreateRequest;
import com.sayyoung.seed.domain.savings.dto.response.SavingsCreateResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsDetailResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsSummaryResponse;
import com.sayyoung.seed.domain.savings.entity.BudgetAllocation;
import com.sayyoung.seed.domain.savings.entity.Savings;
import com.sayyoung.seed.domain.savings.repository.BudgetAllocationRepository;
import com.sayyoung.seed.domain.savings.repository.SavingsQueryRepository;
import com.sayyoung.seed.domain.savings.repository.SavingsRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SavingsService의 목표/누적/속도 계산과 카테고리 검증 로직을 테스트한다.
 */
@ExtendWith(MockitoExtension.class)
class SavingsServiceTest {

    private static final Long USER_ID = 1L;
    private static final String HOUSING_ROOT_ID = "1";
    private static final String WORK_ROOT_ID = "2";

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SavingsRepository savingsRepository;

    @Mock
    private SavingsQueryRepository savingsQueryRepository;

    @Mock
    private BudgetAllocationRepository budgetAllocationRepository;

    private SavingsService savingsService;

    @BeforeEach
    void setUp() {
        savingsService = new SavingsServiceImpl(
                userRepository,
                categoryRepository,
                savingsRepository,
                savingsQueryRepository,
                budgetAllocationRepository
        );
    }

    @Test
    void 모은_돈_요약을_정상적으로_계산한다() {

        // given
        User user = mock(User.class);
        when(user.getProtectionEndDate()).thenReturn(LocalDate.now().plusMonths(10));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Savings housingSaving = savings(HOUSING_ROOT_ID, new BigDecimal("500000"), LocalDate.now());
        Savings workSaving = savings(WORK_ROOT_ID, new BigDecimal("100000"), LocalDate.now());

        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(USER_ID, HOUSING_ROOT_ID))
                .thenReturn(List.of(housingSaving));
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(USER_ID, WORK_ROOT_ID))
                .thenReturn(List.of(workSaving));

        BudgetAllocation housingAllocation = allocation(HOUSING_ROOT_ID, new BigDecimal("3000000"));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of(housingAllocation));

        // when
        SavingsSummaryResponse response = savingsService.getSavings(USER_ID);

        // then
        assertThat(response.getTotal()).isEqualByComparingTo("600000");
        assertThat(response.getCategories()).hasSize(2);
        assertThat(response.getCategories().get(0).getKey()).isEqualTo("housing");
        assertThat(response.getCategories().get(0).getGoal()).isEqualByComparingTo("3000000");
        assertThat(response.getCategories().get(0).getSaved()).isEqualByComparingTo("500000");
        assertThat(response.getCategories().get(1).getKey()).isEqualTo("work");
        assertThat(response.getCategories().get(1).getGoal()).isEqualByComparingTo("0");
    }

    @Test
    void 존재하지_않는_사용자면_예외가_발생한다() {

        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> savingsService.getSavings(USER_ID))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 잘못된_카테고리로_상세를_조회하면_예외가_발생한다() {

        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when & then
        assertThatThrownBy(() -> savingsService.getSavingsDetail(USER_ID, "invalid", pageable))
                .isInstanceOf(BusinessException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void 카테고리_상세를_정상적으로_조회한다() {

        // given
        User user = mock(User.class);
        when(user.getProtectionEndDate()).thenReturn(null);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Savings housingSaving = savings(HOUSING_ROOT_ID, new BigDecimal("500000"), LocalDate.now());
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(USER_ID, HOUSING_ROOT_ID))
                .thenReturn(List.of(housingSaving));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(savingsQueryRepository.findPageByUserIdAndRootCategoryId(eq(USER_ID), eq(HOUSING_ROOT_ID), any()))
                .thenReturn(new PageImpl<>(List.of(housingSaving)));

        // when
        SavingsDetailResponse response = savingsService.getSavingsDetail(USER_ID, "HOUSING", PageRequest.of(0, 20));

        // then
        assertThat(response.getKey()).isEqualTo("housing");
        assertThat(response.getSaved()).isEqualByComparingTo("500000");
        assertThat(response.getRecords().getItems()).hasSize(1);
        assertThat(response.getTrend()).hasSize(1);
    }

    @Test
    void 저축_내역을_등록한다() {

        // given
        User user = mock(User.class);
        Category rootCategory = mock(Category.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(HOUSING_ROOT_ID)).thenReturn(Optional.of(rootCategory));
        when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsCreateRequest request = createRequest("housing", "4월 월세 적립", 500_000L, LocalDate.of(2026, 4, 5));

        // when
        SavingsCreateResponse response = savingsService.createSaving(USER_ID, request);

        // then
        assertThat(response.getCategory()).isEqualTo("housing");
        assertThat(response.getItem()).isEqualTo("4월 월세 적립");
        assertThat(response.getAmount()).isEqualByComparingTo("500000");
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2026, 4, 5));
    }

    @Test
    void 저축_내역_등록시_날짜를_생략하면_오늘_날짜가_사용된다() {

        // given
        User user = mock(User.class);
        Category rootCategory = mock(Category.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(WORK_ROOT_ID)).thenReturn(Optional.of(rootCategory));
        when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsCreateRequest request = createRequest("work", "취업 준비 저축", 100_000L, null);

        // when
        SavingsCreateResponse response = savingsService.createSaving(USER_ID, request);

        // then
        assertThat(response.getDate()).isEqualTo(LocalDate.now());
    }

    /**
     * SavingsQueryRepository는 이미 rootCategoryId로 필터링된 결과를 돌려준다고 가정하고 모킹하므로,
     * 서비스가 실제로 사용하는 amount/savedAt만 스텁한다.
     */
    private Savings savings(
            String rootCategoryId,
            BigDecimal amount,
            LocalDate savedAt
    ) {
        Savings saving = mock(Savings.class);
        when(saving.getAmount()).thenReturn(amount);
        when(saving.getSavedAt()).thenReturn(savedAt);
        return saving;
    }

    private BudgetAllocation allocation(
            String rootCategoryId,
            BigDecimal goalAmount
    ) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(rootCategoryId);

        BudgetAllocation allocation = mock(BudgetAllocation.class);
        when(allocation.getCategory()).thenReturn(category);
        when(allocation.resolveGoalAmount()).thenReturn(goalAmount);
        return allocation;
    }

    /**
     * SavingsCreateRequest는 Jackson 역직렬화 전용(생성자/세터 없음) DTO이므로 리플렉션으로 생성한다.
     */
    private SavingsCreateRequest createRequest(
            String category,
            String item,
            Long amount,
            LocalDate date
    ) {
        try {
            Constructor<SavingsCreateRequest> constructor = SavingsCreateRequest.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SavingsCreateRequest request = constructor.newInstance();

            setField(request, "category", category);
            setField(request, "item", item);
            setField(request, "amount", amount);
            setField(request, "date", date);
            return request;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setField(
            Object target,
            String fieldName,
            Object value
    ) throws ReflectiveOperationException {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

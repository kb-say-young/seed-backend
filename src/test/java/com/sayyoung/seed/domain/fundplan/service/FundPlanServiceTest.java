package com.sayyoung.seed.domain.fundplan.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.RoadmapSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationRequest;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;
import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.savings.entity.BudgetAllocation;
import com.sayyoung.seed.domain.savings.entity.Savings;
import com.sayyoung.seed.domain.savings.repository.BudgetAllocationRepository;
import com.sayyoung.seed.domain.savings.repository.SavingsQueryRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * FundPlanService의 버킷 집계/배분 수정 로직을 테스트한다.
 */
@ExtendWith(MockitoExtension.class)
class FundPlanServiceTest {

    private static final Long USER_ID = 1L;
    private static final String HOUSING_ROOT_ID = "1";
    private static final String WORK_ROOT_ID = "2";
    private static final String LIVING_ROOT_ID = "3";
    private static final String SAVING_ROOT_ID = "4";

    @Mock
    private BudgetAllocationRepository budgetAllocationRepository;

    @Mock
    private SavingsQueryRepository savingsQueryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiagnosisSummaryService diagnosisSummaryService;

    private FundPlanService fundPlanService;

    @BeforeEach
    void setUp() {
        fundPlanService = new FundPlanServiceImpl(
                budgetAllocationRepository,
                savingsQueryRepository,
                categoryRepository,
                userRepository,
                diagnosisSummaryService
        );
    }

    @Test
    void 예산_현황을_정상적으로_계산한다() {

        // given
        stubRoadmap(60, new BigDecimal("12532000"), new BigDecimal("8000000"));

        BudgetAllocation housingAllocation = allocationMock(HOUSING_ROOT_ID, new BigDecimal("40.00"),
                new BigDecimal("1200000"), new BigDecimal("45.00"), new BigDecimal("1350000"));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of(housingAllocation));

        Savings housingSaving = savingsMock(new BigDecimal("1000000"));
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(eq(USER_ID), argThat(id -> !HOUSING_ROOT_ID.equals(id))))
                .thenReturn(List.of());
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(USER_ID, HOUSING_ROOT_ID))
                .thenReturn(List.of(housingSaving));

        // when
        FundPlanResponse response = fundPlanService.getFundPlan(USER_ID);

        // then
        assertThat(response.getTotalFund()).isEqualByComparingTo("12532000");
        assertThat(response.getSecuredAmount()).isEqualByComparingTo("8000000");
        assertThat(response.getShortfallAmount()).isEqualByComparingTo("4532000");
        assertThat(response.getBuckets()).hasSize(4);

        var housingBucket = response.getBuckets().stream()
                .filter(bucket -> bucket.getKey().equals("housing"))
                .findFirst()
                .orElseThrow();
        assertThat(housingBucket.getPct()).isEqualByComparingTo("45.00");
        assertThat(housingBucket.getBudget()).isEqualByComparingTo("1200000");
        assertThat(housingBucket.getAmount()).isEqualByComparingTo("1350000");
        assertThat(housingBucket.getActual()).isEqualByComparingTo("1000000");
        assertThat(housingBucket.getTargetMonths()).isEqualTo(60);

        assertThat(response.getPrinciples()).hasSize(2);
    }

    @Test
    void 배분_비율_합계가_100이_아니면_예외가_발생한다() {

        // given
        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("housing", new BigDecimal("30")),
                allocationRequest("work", new BigDecimal("30"))
        );

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class);

        verify(budgetAllocationRepository, never()).findByUserId(any());
    }

    @Test
    void 잘못된_카테고리_키면_예외가_발생한다() {

        // given
        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("invalid", new BigDecimal("100"))
        );
        stubRoadmap(60, new BigDecimal("10000000"), new BigDecimal("0"));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 매칭되는_리프가_하나면_그대로_비율을_반영한다() {

        // given
        BudgetAllocation housingAllocation = allocationMock(HOUSING_ROOT_ID, new BigDecimal("40.00"),
                new BigDecimal("1200000"), null, null);
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of(housingAllocation));
        stubRoadmap(60, new BigDecimal("10000000"), new BigDecimal("0"));
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(any(), any())).thenReturn(List.of());

        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("housing", new BigDecimal("100"))
        );

        // when
        fundPlanService.updateAllocation(USER_ID, request);

        // then
        ArgumentCaptor<BigDecimal> ratioCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(housingAllocation).updateAllocation(ratioCaptor.capture(), amountCaptor.capture());

        assertThat(ratioCaptor.getValue()).isEqualByComparingTo("100");
        assertThat(amountCaptor.getValue()).isEqualByComparingTo("10000000");
    }

    @Test
    void 매칭되는_리프가_없으면_루트_카테고리로_새_배분을_생성한다() {

        // given
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of());
        stubRoadmap(60, new BigDecimal("10000000"), new BigDecimal("0"));
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(any(), any())).thenReturn(List.of());

        Category rootCategory = mock(Category.class);
        when(categoryRepository.findById(LIVING_ROOT_ID)).thenReturn(Optional.of(rootCategory));

        User userRef = mock(User.class);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userRef);

        when(budgetAllocationRepository.save(any(BudgetAllocation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("living", new BigDecimal("100"))
        );

        // when
        fundPlanService.updateAllocation(USER_ID, request);

        // then
        ArgumentCaptor<BudgetAllocation> captor = ArgumentCaptor.forClass(BudgetAllocation.class);
        verify(budgetAllocationRepository).save(captor.capture());

        BudgetAllocation created = captor.getValue();
        assertThat(created.getCategory()).isEqualTo(rootCategory);
        assertThat(created.getUserRatio()).isEqualByComparingTo("100");
        assertThat(created.getUserAmount()).isEqualByComparingTo("10000000");
    }

    private void stubRoadmap(
            int targetMonths,
            BigDecimal totalCost,
            BigDecimal securedAmount
    ) {
        RoadmapSummaryResponse summary = RoadmapSummaryResponse.of(targetMonths, totalCost, securedAmount);
        MyRoadmapResponse roadmap = MyRoadmapResponse.of(null, null, summary);
        when(diagnosisSummaryService.getMyRoadmap(USER_ID)).thenReturn(roadmap);
    }

    private BudgetAllocation allocationMock(
            String rootCategoryId,
            BigDecimal aiRatio,
            BigDecimal aiAmount,
            BigDecimal userRatio,
            BigDecimal userAmount
    ) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(rootCategoryId);

        BudgetAllocation allocation = mock(BudgetAllocation.class);
        lenient().when(allocation.getCategory()).thenReturn(category);
        lenient().when(allocation.getAiRatio()).thenReturn(aiRatio);
        lenient().when(allocation.getAiAmount()).thenReturn(aiAmount);
        lenient().when(allocation.resolveRatio()).thenReturn(userRatio != null ? userRatio : aiRatio);
        lenient().when(allocation.resolveGoalAmount()).thenReturn(userAmount != null ? userAmount : aiAmount);
        return allocation;
    }

    private Savings savingsMock(
            BigDecimal amount
    ) {
        Savings saving = mock(Savings.class);
        when(saving.getAmount()).thenReturn(amount);
        return saving;
    }

    private FundPlanAllocationUpdateRequest updateRequest(
            FundPlanAllocationRequest... allocations
    ) {
        FundPlanAllocationUpdateRequest request = newInstance(FundPlanAllocationUpdateRequest.class);
        setField(request, "allocations", List.of(allocations));
        return request;
    }

    private FundPlanAllocationRequest allocationRequest(
            String key,
            BigDecimal pct
    ) {
        FundPlanAllocationRequest request = newInstance(FundPlanAllocationRequest.class);
        setField(request, "key", key);
        setField(request, "pct", pct);
        return request;
    }

    /**
     * 요청 DTO는 Jackson 역직렬화 전용(생성자/세터 없음)이라 리플렉션으로 생성한다.
     */
    private <T> T newInstance(
            Class<T> type
    ) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setField(
            Object target,
            String fieldName,
            Object value
    ) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}

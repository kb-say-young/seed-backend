package com.sayyoung.seed.domain.fundplan.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.RoadmapSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationRequest;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;
import com.sayyoung.seed.domain.fundplan.exception.FundPlanErrorCode;
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
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of());

        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("housing", new BigDecimal("30")),
                allocationRequest("work", new BigDecimal("30"))
        );

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class);

        verify(diagnosisSummaryService, never()).getMyRoadmap(any());
    }

    @Test
    void 일부_버킷만_수정해도_전체_배분_합계가_100이_아니면_예외가_발생한다() {

        // given
        // 기존에 housing이 45%를 차지하고 있는 상태에서, 이를 그대로 둔 채 living만 100%로
        // 수정하면 전체 합계가 145%가 되어 저장을 거부해야 한다.
        BudgetAllocation housingAllocation = allocationMock(HOUSING_ROOT_ID, new BigDecimal("40.00"),
                new BigDecimal("1200000"), new BigDecimal("45.00"), new BigDecimal("1350000"));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of(housingAllocation));

        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("living", new BigDecimal("100"))
        );

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode())
                                .isEqualTo(FundPlanErrorCode.ALLOCATION_SUM_INVALID));
    }

    @Test
    void 같은_버킷_키가_중복되면_예외가_발생한다() {

        // given
        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("housing", new BigDecimal("50")),
                allocationRequest("housing", new BigDecimal("50"))
        );

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode())
                                .isEqualTo(FundPlanErrorCode.DUPLICATE_BUCKET_KEY));

        verify(budgetAllocationRepository, never()).findByUserId(any());
    }

    @Test
    void 잘못된_카테고리_키면_예외가_발생한다() {

        // given
        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("invalid", new BigDecimal("100"))
        );

        // when & then
        assertThatThrownBy(() -> fundPlanService.updateAllocation(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode())
                                .isEqualTo(FundPlanErrorCode.INVALID_BUCKET));

        verify(budgetAllocationRepository, never()).findByUserId(any());
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
        // ai_ratio/ai_amount는 DB에서 NOT NULL이므로 "추천 없음"을 뜻하는 0으로 채워져야 한다.
        assertThat(created.getAiRatio()).isEqualByComparingTo("0");
        assertThat(created.getAiAmount()).isEqualByComparingTo("0");
    }

    @Test
    void 매칭되는_리프가_여러개면_반올림해도_배분_합계가_요청값과_정확히_일치한다() {

        // given
        BudgetAllocation leaf1 = leafAllocationMock(WORK_ROOT_ID, new BigDecimal("10.00"));
        BudgetAllocation leaf2 = leafAllocationMock(WORK_ROOT_ID, new BigDecimal("10.00"));
        BudgetAllocation leaf3 = leafAllocationMock(WORK_ROOT_ID, new BigDecimal("10.00"));
        when(budgetAllocationRepository.findByUserId(USER_ID)).thenReturn(List.of(leaf1, leaf2, leaf3));
        stubRoadmap(60, new BigDecimal("10000000"), new BigDecimal("0"));
        when(savingsQueryRepository.findAllByUserIdAndRootCategoryId(any(), any())).thenReturn(List.of());

        FundPlanAllocationUpdateRequest request = updateRequest(
                allocationRequest("work", new BigDecimal("100"))
        );

        // when
        fundPlanService.updateAllocation(USER_ID, request);

        // then
        ArgumentCaptor<BigDecimal> ratio1 = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> amount1 = ArgumentCaptor.forClass(BigDecimal.class);
        verify(leaf1).updateAllocation(ratio1.capture(), amount1.capture());

        ArgumentCaptor<BigDecimal> ratio2 = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> amount2 = ArgumentCaptor.forClass(BigDecimal.class);
        verify(leaf2).updateAllocation(ratio2.capture(), amount2.capture());

        ArgumentCaptor<BigDecimal> ratio3 = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> amount3 = ArgumentCaptor.forClass(BigDecimal.class);
        verify(leaf3).updateAllocation(ratio3.capture(), amount3.capture());

        BigDecimal ratioSum = ratio1.getValue().add(ratio2.getValue()).add(ratio3.getValue());
        BigDecimal amountSum = amount1.getValue().add(amount2.getValue()).add(amount3.getValue());

        // 각 리프의 비율/금액은 독립적으로 반올림되지만, 마지막 리프가 잔여분을 흡수해
        // 합계는 요청한 pct/amount와 정확히 일치해야 한다.
        assertThat(ratioSum).isEqualByComparingTo("100");
        assertThat(amountSum).isEqualByComparingTo("10000000");
    }

    private void stubRoadmap(
            int targetMonths,
            BigDecimal totalCost,
            BigDecimal securedAmount
    ) {
        RoadmapSummaryResponse summary = RoadmapSummaryResponse.of(targetMonths, totalCost, securedAmount);
        MyRoadmapResponse roadmap = MyRoadmapResponse.of(null, null, null, summary);
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

    /**
     * 루트 카테고리 자신이 아니라 그 하위 리프에 매칭되는 배분을 흉내낸다(다중 리프 안분 테스트용).
     */
    private BudgetAllocation leafAllocationMock(
            String rootCategoryId,
            BigDecimal aiRatio
    ) {
        Category root = mock(Category.class);
        lenient().when(root.getId()).thenReturn(rootCategoryId);

        Category leaf = mock(Category.class);
        lenient().when(leaf.getId()).thenReturn("leaf");
        lenient().when(leaf.getParent()).thenReturn(root);

        BudgetAllocation allocation = mock(BudgetAllocation.class);
        lenient().when(allocation.getCategory()).thenReturn(leaf);
        lenient().when(allocation.getAiRatio()).thenReturn(aiRatio);
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

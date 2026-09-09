package com.sayyoung.seed.domain.fundplan.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.RoadmapSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationRequest;
import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanBucketResponse;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanPrincipleResponse;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;
import com.sayyoung.seed.domain.fundplan.entity.FundPlanBucket;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 예산(fund-plan, B1) 조회/배분 수정 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FundPlanServiceImpl implements FundPlanService {

    private static final BigDecimal ALLOCATION_SUM_TARGET = new BigDecimal("100");
    private static final BigDecimal MIN_SAVING_PCT = new BigDecimal("10");

    private final BudgetAllocationRepository budgetAllocationRepository;
    private final SavingsQueryRepository savingsQueryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DiagnosisSummaryService diagnosisSummaryService;

    @Override
    public FundPlanResponse getFundPlan(
            Long userId
    ) {
        List<BudgetAllocation> allocations = budgetAllocationRepository.findByUserId(userId);
        RoadmapSummaryResponse summary = diagnosisSummaryService.getMyRoadmap(userId).getSummary();

        BigDecimal totalFund = summary.getTotalCost();
        BigDecimal securedAmount = summary.getSecuredAmount();
        BigDecimal shortfallAmount = totalFund.subtract(securedAmount).max(BigDecimal.ZERO);

        List<FundPlanBucketResponse> buckets = Arrays.stream(FundPlanBucket.values())
                .map(bucket -> toBucketResponse(bucket, allocations, userId, summary.getTargetMonths()))
                .toList();

        return FundPlanResponse.of(
                totalFund,
                securedAmount,
                shortfallAmount,
                buckets,
                buildPrinciples(buckets)
        );
    }

    @Override
    @Transactional
    public FundPlanResponse updateAllocation(
            Long userId,
            FundPlanAllocationUpdateRequest request
    ) {
        BigDecimal requestedSum = request.getAllocations().stream()
                .map(FundPlanAllocationRequest::getPct)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (requestedSum.compareTo(ALLOCATION_SUM_TARGET) != 0) {
            throw new BusinessException(FundPlanErrorCode.ALLOCATION_SUM_INVALID);
        }

        List<BudgetAllocation> allocations = budgetAllocationRepository.findByUserId(userId);
        BigDecimal totalFund = diagnosisSummaryService.getMyRoadmap(userId).getSummary().getTotalCost();

        for (FundPlanAllocationRequest allocationRequest : request.getAllocations()) {
            FundPlanBucket bucket = FundPlanBucket.fromKey(allocationRequest.getKey())
                    .orElseThrow(() -> new BusinessException(FundPlanErrorCode.INVALID_BUCKET));

            BigDecimal pct = allocationRequest.getPct();
            BigDecimal amount = totalFund.multiply(pct)
                    .divide(ALLOCATION_SUM_TARGET, 0, RoundingMode.HALF_UP);

            applyAllocation(userId, bucket, allocations, pct, amount);
        }

        return getFundPlan(userId);
    }

    /**
     * 한 버킷(루트 카테고리)에 새 비율/금액을 반영한다. 매칭되는 리프가 없으면 루트 카테고리를
     * 직접 참조하는 배분을 새로 만들고, 리프가 여러 개면 기존 AI 추천 비율(ai_ratio) 가중치로 안분한다.
     */
    private void applyAllocation(
            Long userId,
            FundPlanBucket bucket,
            List<BudgetAllocation> allocations,
            BigDecimal pct,
            BigDecimal amount
    ) {
        List<BudgetAllocation> matched = findMatched(allocations, bucket);

        if (matched.isEmpty()) {
            Category root = categoryRepository.findById(bucket.getRootCategoryId())
                    .orElseThrow(() -> new BusinessException(FundPlanErrorCode.INVALID_BUCKET));
            User userRef = userRepository.getReferenceById(userId);

            BudgetAllocation created = BudgetAllocation.create(userRef, root);
            created.updateAllocation(pct, amount);
            budgetAllocationRepository.save(created);
            return;
        }

        if (matched.size() == 1) {
            matched.get(0).updateAllocation(pct, amount);
            return;
        }

        BigDecimal weightSum = matched.stream()
                .map(BudgetAllocation::getAiRatio)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (BudgetAllocation leaf : matched) {
            BigDecimal weight = weightSum.signum() > 0 && leaf.getAiRatio() != null
                    ? leaf.getAiRatio().divide(weightSum, 10, RoundingMode.HALF_UP)
                    : BigDecimal.ONE.divide(BigDecimal.valueOf(matched.size()), 10, RoundingMode.HALF_UP);

            BigDecimal leafRatio = pct.multiply(weight).setScale(2, RoundingMode.HALF_UP);
            BigDecimal leafAmount = amount.multiply(weight).setScale(0, RoundingMode.HALF_UP);
            leaf.updateAllocation(leafRatio, leafAmount);
        }
    }

    private FundPlanBucketResponse toBucketResponse(
            FundPlanBucket bucket,
            List<BudgetAllocation> allocations,
            Long userId,
            int targetMonths
    ) {
        List<BudgetAllocation> matched = findMatched(allocations, bucket);

        BigDecimal pct = matched.stream()
                .map(BudgetAllocation::resolveRatio)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal budget = matched.stream()
                .map(BudgetAllocation::getAiAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amount = matched.stream()
                .map(BudgetAllocation::resolveGoalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal actual = savingsQueryRepository.findAllByUserIdAndRootCategoryId(userId, bucket.getRootCategoryId())
                .stream()
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FundPlanBucketResponse.of(
                bucket.getKey(),
                bucket.getLabel(),
                pct,
                actual,
                budget,
                amount,
                targetMonths
        );
    }

    private List<BudgetAllocation> findMatched(
            List<BudgetAllocation> allocations,
            FundPlanBucket bucket
    ) {
        return allocations.stream()
                .filter(allocation -> matchesRootCategory(allocation.getCategory(), bucket.getRootCategoryId()))
                .toList();
    }

    private boolean matchesRootCategory(
            Category category,
            String rootCategoryId
    ) {
        if (category.getId().equals(rootCategoryId)) {
            return true;
        }
        return category.getParent() != null && rootCategoryId.equals(category.getParent().getId());
    }

    private List<FundPlanPrincipleResponse> buildPrinciples(
            List<FundPlanBucketResponse> buckets
    ) {
        List<FundPlanPrincipleResponse> principles = new ArrayList<>();

        BigDecimal pctSum = buckets.stream()
                .map(FundPlanBucketResponse::getPct)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        principles.add(FundPlanPrincipleResponse.of(
                "배분 비율 합계가 100%예요",
                pctSum.compareTo(ALLOCATION_SUM_TARGET) == 0
        ));

        BigDecimal savingPct = buckets.stream()
                .filter(bucket -> FundPlanBucket.SAVING.getKey().equals(bucket.getKey()))
                .map(FundPlanBucketResponse::getPct)
                .findFirst()
                .orElse(BigDecimal.ZERO);
        principles.add(FundPlanPrincipleResponse.of(
                "저축(금융) 비율이 10% 이상이에요",
                savingPct.compareTo(MIN_SAVING_PCT) >= 0
        ));

        return principles;
    }
}

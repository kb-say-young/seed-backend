package com.sayyoung.seed.domain.savings.service;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.savings.dto.request.SavingsCreateRequest;
import com.sayyoung.seed.domain.savings.dto.response.*;
import com.sayyoung.seed.domain.savings.entity.BudgetAllocation;
import com.sayyoung.seed.domain.savings.entity.Savings;
import com.sayyoung.seed.domain.savings.entity.SavingsCategory;
import com.sayyoung.seed.domain.savings.exception.SavingsErrorCode;
import com.sayyoung.seed.domain.savings.repository.BudgetAllocationRepository;
import com.sayyoung.seed.domain.savings.repository.SavingsQueryRepository;
import com.sayyoung.seed.domain.savings.repository.SavingsRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.PageResponse;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 저축(모은 돈) 조회/등록 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingsServiceImpl implements SavingsService {

    /**
     * users.protection_end_date가 없거나 이미 지난 경우 사용하는 기본 목표 기간(개월)입니다.
     */
    private static final int DEFAULT_REMAINING_MONTHS = 12;

    private static final BigDecimal PACE_AHEAD_MULTIPLIER = new BigDecimal("1.05");
    private static final BigDecimal PACE_BEHIND_MULTIPLIER = new BigDecimal("0.95");

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final String PACE_AHEAD = "ahead";
    private static final String PACE_BEHIND = "behind";
    private static final String PACE_ON_TRACK = "onTrack";

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SavingsRepository savingsRepository;
    private final SavingsQueryRepository savingsQueryRepository;
    private final BudgetAllocationRepository budgetAllocationRepository;

    @Override
    public SavingsSummaryResponse getSavings(
            Long userId
    ) {
        User user = getUser(userId);
        int remainingMonths = resolveRemainingMonths(user);

        CategoryAggregate housing = aggregate(userId, SavingsCategory.HOUSING, remainingMonths);
        CategoryAggregate work = aggregate(userId, SavingsCategory.WORK, remainingMonths);

        BigDecimal total = housing.saved.add(work.saved);
        BigDecimal totalMonthlyGoal = housing.monthlyGoal.add(work.monthlyGoal);
        BigDecimal totalSavedThisMonth = housing.savedThisMonth.add(work.savedThisMonth);
        String pace = derivePace(totalSavedThisMonth, totalMonthlyGoal);

        List<SavingsCategorySummaryResponse> categories = List.of(
                toCategorySummary(housing),
                toCategorySummary(work)
        );

        return SavingsSummaryResponse.of(total, pace, categories);
    }

    @Override
    public SavingsDetailResponse getSavingsDetail(
            Long userId,
            String categoryKey,
            Pageable pageable
    ) {
        SavingsCategory category = resolveCategory(categoryKey);
        User user = getUser(userId);
        int remainingMonths = resolveRemainingMonths(user);

        CategoryAggregate aggregate = aggregate(userId, category, remainingMonths);
        List<SavingsTrendResponse> trend = buildTrend(aggregate.savingsList);

        Page<Savings> recordPage = savingsQueryRepository.findPageByUserIdAndRootCategoryId(
                userId,
                category.getRootCategoryId(),
                pageable
        );
        PageResponse<SavingsRecordResponse> records = PageResponse.from(recordPage.map(SavingsRecordResponse::from));

        return SavingsDetailResponse.of(
                category.getKey(),
                category.getLabel(),
                aggregate.goal,
                aggregate.saved,
                aggregate.monthlyGoal,
                aggregate.savedThisMonth,
                aggregate.note,
                trend,
                records
        );
    }

    @Override
    @Transactional
    public SavingsCreateResponse createSaving(
            Long userId,
            SavingsCreateRequest request
    ) {
        SavingsCategory category = resolveCategory(request.getCategory());
        User user = getUser(userId);
        Category rootCategory = categoryRepository.findById(category.getRootCategoryId())
                .orElseThrow(() -> new BusinessException(SavingsErrorCode.INVALID_CATEGORY));

        LocalDate savedAt = request.getDate() != null ? request.getDate() : LocalDate.now();

        Savings saving = savingsRepository.save(Savings.create(
                rootCategory,
                user,
                request.getItem(),
                BigDecimal.valueOf(request.getAmount()),
                savedAt
        ));

        return SavingsCreateResponse.of(saving, category.getKey());
    }

    private User getUser(
            Long userId
    ) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.UNAUTHORIZED));
    }

    private SavingsCategory resolveCategory(
            String categoryKey
    ) {
        return SavingsCategory.fromKey(categoryKey)
                .orElseThrow(() -> new BusinessException(SavingsErrorCode.INVALID_CATEGORY));
    }

    /**
     * users.protection_end_date를 기준으로 오늘부터 남은 개월 수를 계산합니다.
     * 값이 없거나 이미 지난 경우 DEFAULT_REMAINING_MONTHS를 사용합니다.
     */
    private int resolveRemainingMonths(
            User user
    ) {
        LocalDate protectionEndDate = user.getProtectionEndDate();
        if (protectionEndDate == null) {
            return DEFAULT_REMAINING_MONTHS;
        }

        long months = ChronoUnit.MONTHS.between(LocalDate.now(), protectionEndDate);
        return months > 0 ? (int) months : DEFAULT_REMAINING_MONTHS;
    }

    private CategoryAggregate aggregate(
            Long userId,
            SavingsCategory category,
            int remainingMonths
    ) {
        List<Savings> savingsList = savingsQueryRepository.findAllByUserIdAndRootCategoryId(
                userId,
                category.getRootCategoryId()
        );

        BigDecimal saved = sumAmount(savingsList);
        BigDecimal savedThisMonth = sumAmountForMonth(savingsList, YearMonth.now());
        BigDecimal goal = resolveGoal(userId, category);
        BigDecimal monthlyGoal = calculateMonthlyGoal(goal, saved, remainingMonths);
        String note = buildNote(goal, saved);

        return new CategoryAggregate(category, goal, saved, monthlyGoal, savedThisMonth, note, savingsList);
    }

    private BigDecimal resolveGoal(
            Long userId,
            SavingsCategory category
    ) {
        List<BudgetAllocation> allocations = budgetAllocationRepository.findByUserId(userId);

        return allocations.stream()
                .filter(allocation -> matchesRootCategory(allocation.getCategory(), category.getRootCategoryId()))
                .map(BudgetAllocation::resolveGoalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private BigDecimal sumAmount(
            List<Savings> savingsList
    ) {
        return savingsList.stream()
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumAmountForMonth(
            List<Savings> savingsList,
            YearMonth month
    ) {
        return savingsList.stream()
                .filter(saving -> YearMonth.from(saving.getSavedAt()).equals(month))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 이번 달 목표 = (목표 금액 - 누적 저축액) ÷ 남은 개월 수.
     */
    private BigDecimal calculateMonthlyGoal(
            BigDecimal goal,
            BigDecimal saved,
            int remainingMonths
    ) {
        if (remainingMonths <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal remainingAmount = goal.subtract(saved).max(BigDecimal.ZERO);
        return remainingAmount.divide(BigDecimal.valueOf(remainingMonths), 0, RoundingMode.CEILING);
    }

    /**
     * 이번 달 실제 저축액과 이번 달 목표를 비교해 속도를 판단합니다.
     * 이번 달 목표가 0이면(이미 목표를 달성한 경우 등) 항상 onTrack으로 응답합니다.
     */
    private String derivePace(
            BigDecimal savedThisMonth,
            BigDecimal monthlyGoal
    ) {
        if (monthlyGoal.signum() == 0) {
            return PACE_ON_TRACK;
        }
        if (savedThisMonth.compareTo(monthlyGoal.multiply(PACE_AHEAD_MULTIPLIER)) >= 0) {
            return PACE_AHEAD;
        }
        if (savedThisMonth.compareTo(monthlyGoal.multiply(PACE_BEHIND_MULTIPLIER)) <= 0) {
            return PACE_BEHIND;
        }
        return PACE_ON_TRACK;
    }

    private String buildNote(
            BigDecimal goal,
            BigDecimal saved
    ) {
        if (goal.signum() <= 0) {
            return "목표 금액이 아직 설정되지 않았어요.";
        }

        BigDecimal remaining = goal.subtract(saved).max(BigDecimal.ZERO);
        if (remaining.signum() == 0) {
            return "목표를 달성했어요!";
        }
        return String.format("목표까지 %,d원 남았어요.", remaining.longValueExact());
    }

    private List<SavingsTrendResponse> buildTrend(
            List<Savings> savingsList
    ) {
        Map<YearMonth, BigDecimal> monthlySums = savingsList.stream()
                .collect(Collectors.groupingBy(
                        saving -> YearMonth.from(saving.getSavedAt()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Savings::getAmount, BigDecimal::add)
                ));

        List<SavingsTrendResponse> trend = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (Map.Entry<YearMonth, BigDecimal> entry : monthlySums.entrySet()) {
            cumulative = cumulative.add(entry.getValue());
            trend.add(SavingsTrendResponse.of(entry.getKey().format(MONTH_FORMATTER), cumulative));
        }
        return trend;
    }

    private SavingsCategorySummaryResponse toCategorySummary(
            CategoryAggregate aggregate
    ) {
        return SavingsCategorySummaryResponse.of(
                aggregate.category.getKey(),
                aggregate.category.getLabel(),
                aggregate.goal,
                aggregate.saved,
                aggregate.monthlyGoal,
                aggregate.note
        );
    }

    /**
     * 카테고리 하나(housing 또는 work)에 대한 목표/누적/이번 달 계산 결과를 담는 내부 전용 값 객체입니다.
     */
    private static class CategoryAggregate {

        private final SavingsCategory category;
        private final BigDecimal goal;
        private final BigDecimal saved;
        private final BigDecimal monthlyGoal;
        private final BigDecimal savedThisMonth;
        private final String note;
        private final List<Savings> savingsList;

        private CategoryAggregate(
                SavingsCategory category,
                BigDecimal goal,
                BigDecimal saved,
                BigDecimal monthlyGoal,
                BigDecimal savedThisMonth,
                String note,
                List<Savings> savingsList
        ) {
            this.category = category;
            this.goal = goal;
            this.saved = saved;
            this.monthlyGoal = monthlyGoal;
            this.savedThisMonth = savedThisMonth;
            this.note = note;
            this.savingsList = savingsList;
        }
    }
}

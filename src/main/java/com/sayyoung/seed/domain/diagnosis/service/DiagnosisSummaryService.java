package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.MyRoadmapResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.RoadmapSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * 로드맵 화면 상단에 노출되는 진단 요약 정보를 계산합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisSummaryService {

    private static final String WEEK_UNIT = "week";
    private static final int WEEKS_PER_MONTH = 4;
    private static final String SAVING_AMOUNT_TYPE = "saving";
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    /**
     * 진단 요약 정보를 조회 시점에 계산합니다.
     *
     * @param diagnosisId 진단 식별자
     * @throws BusinessException 존재하지 않는 진단 식별자인 경우
     */
    public DiagnosisSummaryResponse getSummary(
            Long diagnosisId
    ) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        List<Recommendation> recommendations = recommendationRepository.findByDiagnosisId(diagnosisId);

        int targetMonths = calculateTargetMonths(recommendations);
        int elapsedMonths = calculateElapsedMonths(diagnosis);
        int remainingMonths = Math.max(0, targetMonths - elapsedMonths);
        BigDecimal fixedBudget = resolveFixedBudget(diagnosis.getUser());
        BigDecimal monthlyTargetSaving = calculateMonthlyTargetSaving(fixedBudget, recommendations, remainingMonths);

        return DiagnosisSummaryResponse.of(
                targetMonths,
                elapsedMonths,
                remainingMonths,
                monthlyTargetSaving
        );
    }

    /**
     * 사용자가 가장 최근에 생성한 진단을 기준으로 로드맵 화면 상단(런웨이 바) 요약을 계산합니다.
     *
     * @param userId 사용자 식별자
     * @throws BusinessException 사용자를 찾을 수 없거나(UNAUTHORIZED), 진단이 없는 경우(DIAGNOSIS_NOT_FOUND)
     */
    public MyRoadmapResponse getMyRoadmap(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.UNAUTHORIZED));
        Diagnosis diagnosis = diagnosisRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new BusinessException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        List<Recommendation> recommendations = recommendationRepository.findByDiagnosisId(diagnosis.getId());

        int targetMonths = calculateTargetMonths(recommendations);
        BigDecimal totalCost = calculateTotalSavingTargetAmount(recommendations);
        BigDecimal securedAmount = resolveFixedBudget(user);

        String protectionEndYm = user.getProtectionEndDate() != null
                ? user.getProtectionEndDate().format(YEAR_MONTH_FORMATTER)
                : null;
        String planUntilYm = targetMonths > 0 && diagnosis.getCreatedAt() != null
                ? diagnosis.getCreatedAt().toLocalDate().plusMonths(targetMonths).format(YEAR_MONTH_FORMATTER)
                : null;

        return MyRoadmapResponse.of(
                protectionEndYm,
                planUntilYm,
                RoadmapSummaryResponse.of(targetMonths, totalCost, securedAmount)
        );
    }

    private BigDecimal resolveFixedBudget(
            User user
    ) {
        return user.getFixedBudget() != null ? user.getFixedBudget() : BigDecimal.ZERO;
    }

    /**
     * 각 추천 항목의 (시작 오프셋 + 기간) 중 가장 늦게 끝나는 값을 목표 개월 수로 계산합니다.
     */
    private int calculateTargetMonths(
            List<Recommendation> recommendations
    ) {
        return recommendations.stream()
                .mapToInt(recommendation ->
                        toMonths(recommendation.getStartOffsetValue(), recommendation.getStartOffsetUnit())
                                + toMonths(recommendation.getDurationValue(), recommendation.getDurationUnit()))
                .max()
                .orElse(0);
    }

    private int toMonths(
            Integer value,
            String unit
    ) {
        if (WEEK_UNIT.equalsIgnoreCase(unit)) {
            return (int) Math.ceil(value / (double) WEEKS_PER_MONTH);
        }
        return value;
    }

    private int calculateElapsedMonths(
            Diagnosis diagnosis
    ) {
        if (diagnosis.getCreatedAt() == null) {
            return 0;
        }
        return (int) ChronoUnit.MONTHS.between(diagnosis.getCreatedAt().toLocalDate(), LocalDate.now());
    }

    /**
     * 누적액은 diagnosis별 저축 진행률이 아니라 users.fixed_budget(디딤씨앗통장 잔액) 값을
     * 그대로 사용하는 임시 방편이다. 사용자당 하나뿐이라 여러 진단에 동일한 값이 반영되고,
     * 체크리스트 완료 등 실제 진행 상황과는 연동되지 않는다. 진단별 누적액 추적은 별도
     * 예산 기능 이슈에서 다룬다.
     */
    private BigDecimal calculateMonthlyTargetSaving(
            BigDecimal fixedBudget,
            List<Recommendation> recommendations,
            int remainingMonths
    ) {
        if (remainingMonths <= 0) {
            return null;
        }

        BigDecimal totalTargetAmount = calculateTotalSavingTargetAmount(recommendations);
        BigDecimal remainingAmount = totalTargetAmount.subtract(fixedBudget).max(BigDecimal.ZERO);

        return remainingAmount.divide(BigDecimal.valueOf(remainingMonths), 0, RoundingMode.CEILING);
    }

    /**
     * amount_type이 saving인 추천 항목의 target_amount 합계를 계산합니다.
     */
    private BigDecimal calculateTotalSavingTargetAmount(
            List<Recommendation> recommendations
    ) {
        return recommendations.stream()
                .filter(recommendation -> SAVING_AMOUNT_TYPE.equals(recommendation.getAmountType()))
                .map(Recommendation::getTargetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

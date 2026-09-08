package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.entity.Recommendation;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.diagnosis.repository.RecommendationRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;

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
        BigDecimal monthlyTargetSaving = calculateMonthlyTargetSaving(diagnosis, recommendations, remainingMonths);

        return DiagnosisSummaryResponse.of(
                targetMonths,
                elapsedMonths,
                remainingMonths,
                monthlyTargetSaving
        );
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
        if (WEEK_UNIT.equals(unit)) {
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

    private BigDecimal calculateMonthlyTargetSaving(
            Diagnosis diagnosis,
            List<Recommendation> recommendations,
            int remainingMonths
    ) {
        if (remainingMonths <= 0) {
            return null;
        }

        BigDecimal totalTargetAmount = recommendations.stream()
                .map(Recommendation::getTargetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal accumulatedAmount = diagnosis.getUser().getFixedBudget() != null
                ? diagnosis.getUser().getFixedBudget()
                : BigDecimal.ZERO;

        return totalTargetAmount.subtract(accumulatedAmount)
                .divide(BigDecimal.valueOf(remainingMonths), 0, RoundingMode.CEILING);
    }
}

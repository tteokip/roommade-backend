package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse.Status;
import com.roommade.global.exception.BusinessException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class RirCalculator {

    private static final int INTERNAL_SCALE = 10;
    private static final int RESPONSE_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal NORMAL_RIR_LIMIT = BigDecimal.valueOf(30);
    private static final BigDecimal SEVERE_RIR_LIMIT = BigDecimal.valueOf(50);
    private static final BigDecimal RIR_RANGE = BigDecimal.valueOf(20);
    private static final BigDecimal RIR_SCORE_WEIGHT = new BigDecimal("0.45");
    private static final BigDecimal TARGET_RIR_RATE = new BigDecimal("0.30");
    private static final int MAX_SCORE = 45;
    private static final int TARGET_RIR_PERCENT = 30;

    /** 원 단위 월 소득과 월세를 기반으로 RIR 진단 결과를 계산한다. */
    public RirDiagnosisResponse calculate(Long monthlyIncome, Long monthlyRent) {
        validate(monthlyIncome, monthlyRent);

        BigDecimal income = BigDecimal.valueOf(monthlyIncome);
        BigDecimal rent = BigDecimal.valueOf(monthlyRent);
        BigDecimal rir = calculateRir(income, rent);
        BigDecimal achievementRate = calculateAchievementRate(income, rent);
        BigDecimal score = achievementRate.multiply(RIR_SCORE_WEIGHT);
        long targetMonthlyRent = income.multiply(TARGET_RIR_RATE)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        long requiredRentReduction = Math.max(monthlyRent - targetMonthlyRent, 0L);

        return new RirDiagnosisResponse(
                monthlyIncome,
                monthlyRent,
                toResponseScale(rir),
                toResponseScale(achievementRate),
                toResponseScale(score),
                MAX_SCORE,
                TARGET_RIR_PERCENT,
                determineStatus(income, rent),
                targetMonthlyRent,
                requiredRentReduction);
    }

    private void validate(Long monthlyIncome, Long monthlyRent) {
        if (monthlyIncome == null
                || monthlyRent == null
                || monthlyIncome <= 0
                || monthlyRent <= 0) {
            throw new BusinessException(PreparationErrorCode.RIR_NOT_CALCULABLE);
        }
    }

    private BigDecimal calculateRir(BigDecimal monthlyIncome, BigDecimal monthlyRent) {
        return monthlyRent.multiply(ONE_HUNDRED)
                .divide(monthlyIncome, INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAchievementRate(
            BigDecimal monthlyIncome, BigDecimal monthlyRent) {
        if (compareRirToLimit(monthlyIncome, monthlyRent, NORMAL_RIR_LIMIT) <= 0) {
            return ONE_HUNDRED;
        }
        if (compareRirToLimit(monthlyIncome, monthlyRent, SEVERE_RIR_LIMIT) >= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal numerator = monthlyIncome.multiply(SEVERE_RIR_LIMIT)
                .subtract(monthlyRent.multiply(ONE_HUNDRED))
                .multiply(ONE_HUNDRED);
        BigDecimal denominator = monthlyIncome.multiply(RIR_RANGE);
        return numerator.divide(denominator, INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    private Status determineStatus(BigDecimal monthlyIncome, BigDecimal monthlyRent) {
        if (compareRirToLimit(monthlyIncome, monthlyRent, NORMAL_RIR_LIMIT) <= 0) {
            return Status.NORMAL;
        }
        if (compareRirToLimit(monthlyIncome, monthlyRent, SEVERE_RIR_LIMIT) >= 0) {
            return Status.SEVERE;
        }
        return Status.EXCESSIVE;
    }

    private int compareRirToLimit(
            BigDecimal monthlyIncome, BigDecimal monthlyRent, BigDecimal rirLimit) {
        return monthlyRent.multiply(ONE_HUNDRED)
                .compareTo(monthlyIncome.multiply(rirLimit));
    }

    private BigDecimal toResponseScale(BigDecimal value) {
        return value.setScale(RESPONSE_SCALE, RoundingMode.HALF_UP);
    }
}

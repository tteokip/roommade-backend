package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.HouseComparisonProgressResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse.Status;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import com.roommade.global.exception.BusinessException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreparationServiceImpl implements PreparationService {

    private static final long WON_PER_TEN_THOUSAND_WON = 10_000L;
    private static final int INTERNAL_SCALE = 10;
    private static final int RESPONSE_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal NORMAL_RIR_LIMIT = BigDecimal.valueOf(30);
    private static final BigDecimal SEVERE_RIR_LIMIT = BigDecimal.valueOf(50);
    private static final BigDecimal RIR_RANGE = BigDecimal.valueOf(20);
    private static final BigDecimal RIR_SCORE_WEIGHT = new BigDecimal("0.45");
    private static final BigDecimal DEPOSIT_SCORE_WEIGHT = new BigDecimal("0.45");
    private static final int MAX_SCORE = 45;
    private static final int HOUSE_COMPARISON_MAX_SCORE = 10;
    private static final int TARGET_RIR_PERCENT = 30;

    private final PreparationMapper preparationMapper;

    /** 사용자 프로필 조회 및 RIR 진단 결과 생성. */
    @Override
    public RirDiagnosisResponse getRirDiagnosis(Long userId) {
        RirProfileResponse profile = preparationMapper.findRirProfileByUserId(userId);
        if (profile == null) {
            throw new BusinessException(PreparationErrorCode.RIR_DATA_NOT_FOUND);
        }

        validateRirProfile(profile);

        BigDecimal monthlyIncome = BigDecimal.valueOf(profile.getMonthlyIncome());
        BigDecimal monthlyRent = BigDecimal.valueOf(profile.getMonthlyRentLimit());
        BigDecimal rir = calculateRir(monthlyIncome, monthlyRent);
        BigDecimal achievementRate = calculateAchievementRate(monthlyIncome, monthlyRent);
        BigDecimal score = achievementRate.multiply(RIR_SCORE_WEIGHT);

        long monthlyIncomeWon = toWon(profile.getMonthlyIncome());
        long monthlyRentWon = toWon(profile.getMonthlyRentLimit());
        long targetMonthlyRentWon = Math.multiplyExact(profile.getMonthlyIncome(), 3_000L);
        long requiredRentReductionWon = Math.max(monthlyRentWon - targetMonthlyRentWon, 0L);

        return new RirDiagnosisResponse(
                monthlyIncomeWon,
                monthlyRentWon,
                toResponseScale(rir),
                toResponseScale(achievementRate),
                toResponseScale(score),
                MAX_SCORE,
                TARGET_RIR_PERCENT,
                determineStatus(monthlyIncome, monthlyRent),
                targetMonthlyRentWon,
                requiredRentReductionWon);
    }

    /** 사용자 보증금 원천 데이터 조회 및 마련 현황 생성. */
    @Override
    public DepositProgressResponse getDepositProgress(Long userId) {
        DepositProgressSourceResponse source =
                preparationMapper.findDepositProgressByUserId(userId);
        if (source == null) {
            throw new BusinessException(PreparationErrorCode.DEPOSIT_DATA_NOT_FOUND);
        }

        validateDepositProgress(source);

        BigDecimal targetDeposit = BigDecimal.valueOf(source.getTargetDepositWon());
        BigDecimal currentDeposit = BigDecimal.valueOf(source.getCurrentDepositWon());
        BigDecimal achievementRate = calculateDepositAchievementRate(
                targetDeposit, currentDeposit);
        BigDecimal score = achievementRate.multiply(DEPOSIT_SCORE_WEIGHT);
        long remainingDepositWon = Math.max(
                source.getTargetDepositWon() - source.getCurrentDepositWon(), 0L);

        return new DepositProgressResponse(
                source.getTargetDepositWon(),
                source.getCurrentDepositWon(),
                toResponseScale(achievementRate),
                toResponseScale(score),
                MAX_SCORE,
                remainingDepositWon);
    }

    /** 사용자 집 비교 완료 여부 기반 점수 생성. */
    @Override
    public HouseComparisonProgressResponse getHouseComparisonProgress(Long userId) {
        LocalDateTime completedAt =
                preparationMapper.findHouseComparisonCompletedAtByUserId(userId);
        int score = completedAt == null
                ? 0
                : HOUSE_COMPARISON_MAX_SCORE;
        return new HouseComparisonProgressResponse(
                score,
                HOUSE_COMPARISON_MAX_SCORE,
                completedAt);
    }

    /** 사용자 최초 비교 매물 등록 완료 시간 기록. */
    @Override
    @Transactional
    public void markHouseComparisonCompleted(Long userId) {
        preparationMapper.markHouseComparisonCompleted(userId);
    }

    /** 보증금 계산에 필요한 목표 금액과 현재 마련 금액 유효성 검증. */
    private void validateDepositProgress(DepositProgressSourceResponse source) {
        if (source.getTargetDepositWon() == null
                || source.getCurrentDepositWon() == null
                || source.getTargetDepositWon() <= 0
                || source.getCurrentDepositWon() < 0) {
            throw new BusinessException(PreparationErrorCode.DEPOSIT_NOT_CALCULABLE);
        }
    }

    /** 목표 보증금 대비 현재 마련 금액 달성률 계산. */
    private BigDecimal calculateDepositAchievementRate(
            BigDecimal targetDeposit, BigDecimal currentDeposit) {
        if (currentDeposit.compareTo(targetDeposit) >= 0) {
            return ONE_HUNDRED;
        }
        return currentDeposit.multiply(ONE_HUNDRED)
                .divide(targetDeposit, INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    /** RIR 계산에 필요한 월 소득과 예상 월세 유효성 검증. */
    private void validateRirProfile(RirProfileResponse profile) {
        if (profile.getMonthlyIncome() == null
                || profile.getMonthlyRentLimit() == null
                || profile.getMonthlyIncome() <= 0
                || profile.getMonthlyRentLimit() <= 0) {
            throw new BusinessException(PreparationErrorCode.RIR_NOT_CALCULABLE);
        }
    }

    /** 월 소득 대비 예상 월세 비율 계산. */
    private BigDecimal calculateRir(BigDecimal monthlyIncome, BigDecimal monthlyRent) {
        return monthlyRent.multiply(ONE_HUNDRED)
                .divide(monthlyIncome, INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    /** 목표 RIR 30% 기준 달성률 계산. */
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

    /** RIR 구간별 주거비 부담 상태 판정.
     * RIR이 30% 이하면 NORMAL,
     * 30% 초과 50% 미만이면 EXCESSIVE,
     * 50% 이상이면 SEVERE
     */
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

    private long toWon(long amountInTenThousandWon) {
        return Math.multiplyExact(amountInTenThousandWon, WON_PER_TEN_THOUSAND_WON);
    }

    private BigDecimal toResponseScale(BigDecimal value) {
        return value.setScale(RESPONSE_SCALE, RoundingMode.HALF_UP);
    }
}

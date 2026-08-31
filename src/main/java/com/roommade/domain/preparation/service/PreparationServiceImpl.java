package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.HouseComparisonProgressResponse;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
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

    private static final int INTERNAL_SCALE = 10;
    private static final int RESPONSE_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal DEPOSIT_SCORE_WEIGHT = new BigDecimal("0.45");
    private static final int MAX_SCORE = 45;
    private static final int HOUSE_COMPARISON_MAX_SCORE = 10;

    private final PreparationMapper preparationMapper;
    private final RirCalculator rirCalculator;

    /** 사용자 프로필 조회 및 RIR 진단 결과 생성. */
    @Override
    public RirDiagnosisResponse getRirDiagnosis(Long userId) {
        RirProfileResponse profile = preparationMapper.findRirProfileByUserId(userId);
        if (profile == null) {
            throw new BusinessException(PreparationErrorCode.RIR_DATA_NOT_FOUND);
        }

        return rirCalculator.calculate(
                profile.getMonthlyIncome(), profile.getMonthlyRentLimit());
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

        BigDecimal targetDeposit = BigDecimal.valueOf(source.getTargetDeposit());
        BigDecimal currentDeposit = BigDecimal.valueOf(source.getCurrentDeposit());
        BigDecimal achievementRate = calculateDepositAchievementRate(
                targetDeposit, currentDeposit);
        BigDecimal score = achievementRate.multiply(DEPOSIT_SCORE_WEIGHT);
        long remainingDeposit = Math.max(
                source.getTargetDeposit() - source.getCurrentDeposit(), 0L);

        return new DepositProgressResponse(
                source.getTargetDeposit(),
                source.getCurrentDeposit(),
                toResponseScale(achievementRate),
                toResponseScale(score),
                MAX_SCORE,
                remainingDeposit);
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

    /** 사용자 RIR·보증금·집 비교 점수 기반 자립 준비도 전체 진단 결과 생성. */
    @Override
    public ReadinessDiagnosisResponse getReadinessDiagnosis(Long userId) {
        RirDiagnosisResponse rir = getRirDiagnosis(userId);
        DepositProgressResponse deposit = getDepositProgress(userId);
        HouseComparisonProgressResponse houseComparison =
                getHouseComparisonProgress(userId);

        int maxScore = rir.getMaxScore()
                + deposit.getMaxScore()
                + houseComparison.getMaxScore();
        BigDecimal readinessScore = rir.getScore()
                .add(deposit.getScore())
                .add(BigDecimal.valueOf(houseComparison.getHouseComparisonScore()));

        if (preparationMapper.findHouseConfirmedAtByUserId(userId) != null) {
            readinessScore = BigDecimal.valueOf(maxScore)
                    .setScale(RESPONSE_SCALE, RoundingMode.HALF_UP);
        }

        return new ReadinessDiagnosisResponse(
                readinessScore,
                maxScore,
                rir.getScore(),
                rir.getMaxScore(),
                deposit.getScore(),
                deposit.getMaxScore(),
                houseComparison.getHouseComparisonScore(),
                houseComparison.getMaxScore());
    }

    /** 사용자 최초 비교 매물 등록 완료 시간 기록. */
    @Override
    @Transactional
    public void markHouseComparisonCompleted(Long userId) {
        preparationMapper.markHouseComparisonCompleted(userId);
    }

    /** 사용자 집 확정 매물 및 독립 후 전환 시간 기록. */
    @Override
    @Transactional
    public LocalDateTime confirmHouse(Long userId, Long confirmedHouseId) {
        int updatedRows = preparationMapper.updateHouseConfirmation(userId, confirmedHouseId);
        if (updatedRows == 0) {
            if (!preparationMapper.existsIndependenceProgressByUserId(userId)) {
                throw new BusinessException(
                        PreparationErrorCode.INDEPENDENCE_PROGRESS_NOT_FOUND);
            }
            throw new BusinessException(PreparationErrorCode.HOUSE_ALREADY_CONFIRMED);
        }
        return preparationMapper.findHouseConfirmedAtByUserId(userId);
    }

    /** 보증금 계산에 필요한 목표 금액과 현재 마련 금액 유효성 검증. */
    private void validateDepositProgress(DepositProgressSourceResponse source) {
        if (source.getTargetDeposit() == null
                || source.getCurrentDeposit() == null
                || source.getTargetDeposit() <= 0
                || source.getCurrentDeposit() < 0) {
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

    private BigDecimal toResponseScale(BigDecimal value) {
        return value.setScale(RESPONSE_SCALE, RoundingMode.HALF_UP);
    }
}

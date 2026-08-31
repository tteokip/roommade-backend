package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse.Status;
import com.roommade.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RirCalculatorTest {

    private final RirCalculator rirCalculator = new RirCalculator();

    @Test
    @DisplayName("원 단위 월 소득과 월세로 과도 상태와 달성률·점수·절감액을 계산한다")
    void calculatesExcessiveRirDiagnosis() {
        RirDiagnosisResponse result = rirCalculator.calculate(1_870_000L, 650_000L);

        assertThat(result.getMonthlyIncome()).isEqualTo(1_870_000L);
        assertThat(result.getExpectedMonthlyRent()).isEqualTo(650_000L);
        assertThat(result.getRirPercent()).isEqualByComparingTo("34.76");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("76.20");
        assertThat(result.getScore()).isEqualByComparingTo("34.29");
        assertThat(result.getMaxScore()).isEqualTo(45);
        assertThat(result.getTargetRirPercent()).isEqualTo(30);
        assertThat(result.getStatus()).isEqualTo(Status.EXCESSIVE);
        assertThat(result.getTargetMonthlyRent()).isEqualTo(561_000L);
        assertThat(result.getRequiredRentReduction()).isEqualTo(89_000L);
    }

    @Test
    @DisplayName("RIR이 정확히 30%이면 일반 상태, 달성률 100%, 45점이다")
    void calculatesNormalDiagnosisAtThirtyPercentBoundary() {
        RirDiagnosisResponse result = rirCalculator.calculate(2_000_000L, 600_000L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("30.00");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("100.00");
        assertThat(result.getScore()).isEqualByComparingTo("45.00");
        assertThat(result.getStatus()).isEqualTo(Status.NORMAL);
        assertThat(result.getRequiredRentReduction()).isZero();
    }

    @Test
    @DisplayName("RIR이 정확히 50%이면 심각 상태, 달성률과 점수가 0이다")
    void calculatesSevereDiagnosisAtFiftyPercentBoundary() {
        RirDiagnosisResponse result = rirCalculator.calculate(1_000_000L, 500_000L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("50.00");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("0.00");
        assertThat(result.getScore()).isEqualByComparingTo("0.00");
        assertThat(result.getStatus()).isEqualTo(Status.SEVERE);
        assertThat(result.getTargetMonthlyRent()).isEqualTo(300_000L);
        assertThat(result.getRequiredRentReduction()).isEqualTo(200_000L);
    }

    @Test
    @DisplayName("RIR이 30% 미만이면 필요한 월세 절감액은 0원이다")
    void doesNotReturnNegativeReductionWhenTargetIsAlreadyMet() {
        RirDiagnosisResponse result = rirCalculator.calculate(2_000_000L, 500_000L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("25.00");
        assertThat(result.getStatus()).isEqualTo(Status.NORMAL);
        assertThat(result.getTargetMonthlyRent()).isEqualTo(600_000L);
        assertThat(result.getRequiredRentReduction()).isZero();
    }

    @Test
    @DisplayName("월 소득 또는 월세가 없거나 0 이하이면 RIR_NOT_CALCULABLE 예외를 던진다")
    void throwsWhenAmountIsNotCalculable() {
        assertThatThrownBy(() -> rirCalculator.calculate(0L, 650_000L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.RIR_NOT_CALCULABLE);
    }
}

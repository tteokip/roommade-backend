package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse.Status;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import com.roommade.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreparationServiceImplTest {

    @Mock
    private PreparationMapper preparationMapper;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @Test
    @DisplayName("RIR 30~50%이면 과도 상태와 달성률·점수·원 단위 절감액을 계산한다")
    void calculatesExcessiveRirDiagnosis() {
        Long userId = 1L;
        when(preparationMapper.findRirProfileByUserId(userId))
                .thenReturn(new RirProfileResponse(187L, 65L));

        RirDiagnosisResponse result = preparationService.getRirDiagnosis(userId);

        assertThat(result.getMonthlyIncomeWon()).isEqualTo(1_870_000L);
        assertThat(result.getExpectedMonthlyRentWon()).isEqualTo(650_000L);
        assertThat(result.getRirPercent()).isEqualByComparingTo("34.76");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("76.20");
        assertThat(result.getScore()).isEqualByComparingTo("34.29");
        assertThat(result.getMaxScore()).isEqualTo(45);
        assertThat(result.getTargetRirPercent()).isEqualTo(30);
        assertThat(result.getStatus()).isEqualTo(Status.EXCESSIVE);
        assertThat(result.getTargetMonthlyRentWon()).isEqualTo(561_000L);
        assertThat(result.getRequiredRentReductionWon()).isEqualTo(89_000L);
        verify(preparationMapper).findRirProfileByUserId(userId);
    }

    @Test
    @DisplayName("RIR이 정확히 30%이면 일반 상태, 달성률 100%, 45점이고 절감액은 0원이다")
    void calculatesNormalDiagnosisAtThirtyPercentBoundary() {
        when(preparationMapper.findRirProfileByUserId(1L))
                .thenReturn(new RirProfileResponse(200L, 60L));

        RirDiagnosisResponse result = preparationService.getRirDiagnosis(1L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("30.00");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("100.00");
        assertThat(result.getScore()).isEqualByComparingTo("45.00");
        assertThat(result.getStatus()).isEqualTo(Status.NORMAL);
        assertThat(result.getRequiredRentReductionWon()).isZero();
    }

    @Test
    @DisplayName("RIR이 정확히 50%이면 심각 상태, 달성률과 점수가 0이다")
    void calculatesSevereDiagnosisAtFiftyPercentBoundary() {
        when(preparationMapper.findRirProfileByUserId(1L))
                .thenReturn(new RirProfileResponse(100L, 50L));

        RirDiagnosisResponse result = preparationService.getRirDiagnosis(1L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("50.00");
        assertThat(result.getAchievementRate()).isEqualByComparingTo("0.00");
        assertThat(result.getScore()).isEqualByComparingTo("0.00");
        assertThat(result.getStatus()).isEqualTo(Status.SEVERE);
        assertThat(result.getTargetMonthlyRentWon()).isEqualTo(300_000L);
        assertThat(result.getRequiredRentReductionWon()).isEqualTo(200_000L);
    }

    @Test
    @DisplayName("RIR이 30% 미만이면 목표 월세보다 현재 월세가 낮아도 절감액은 0원이다")
    void doesNotReturnNegativeReductionWhenTargetIsAlreadyMet() {
        when(preparationMapper.findRirProfileByUserId(1L))
                .thenReturn(new RirProfileResponse(200L, 50L));

        RirDiagnosisResponse result = preparationService.getRirDiagnosis(1L);

        assertThat(result.getRirPercent()).isEqualByComparingTo("25.00");
        assertThat(result.getStatus()).isEqualTo(Status.NORMAL);
        assertThat(result.getTargetMonthlyRentWon()).isEqualTo(600_000L);
        assertThat(result.getRequiredRentReductionWon()).isZero();
    }

    @Test
    @DisplayName("사용자 프로필이 없으면 RIR_DATA_NOT_FOUND 예외를 던진다")
    void throwsWhenRirProfileDoesNotExist() {
        when(preparationMapper.findRirProfileByUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> preparationService.getRirDiagnosis(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.RIR_DATA_NOT_FOUND);
    }

    @Test
    @DisplayName("월 소득 또는 예상 월세가 0 이하이면 RIR_NOT_CALCULABLE 예외를 던진다")
    void throwsWhenRirProfileContainsNonPositiveAmount() {
        when(preparationMapper.findRirProfileByUserId(1L))
                .thenReturn(new RirProfileResponse(0L, 65L));

        assertThatThrownBy(() -> preparationService.getRirDiagnosis(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.RIR_NOT_CALCULABLE);
    }
}

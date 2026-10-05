package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import com.roommade.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepositProgressServiceImplTest {

    @Mock
    private PreparationMapper preparationMapper;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @Test
    @DisplayName("현재 보증금으로 달성률·점수·원 단위 부족 금액을 계산한다")
    void calculatesDepositProgress() {
        Long userId = 1L;
        when(preparationMapper.findDepositProgressByUserId(userId))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 35_123_456L));

        DepositProgressResponse result = preparationService.getDepositProgress(userId);

        assertThat(result.getTargetDeposit()).isEqualTo(50_000_000L);
        assertThat(result.getCurrentDeposit()).isEqualTo(35_123_456L);
        assertThat(result.getAchievementRate()).isEqualByComparingTo("70.25");
        assertThat(result.getScore()).isEqualByComparingTo("31.61");
        assertThat(result.getMaxScore()).isEqualTo(45);
        assertThat(result.getRemainingDeposit()).isEqualTo(14_876_544L);
        verify(preparationMapper).findDepositProgressByUserId(userId);
    }

    @Test
    @DisplayName("현재 보증금이 0원이면 달성률과 점수는 0이고 목표 금액 전체가 남는다")
    void returnsZeroProgressWhenCurrentDepositIsZero() {
        when(preparationMapper.findDepositProgressByUserId(1L))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 0L));

        DepositProgressResponse result = preparationService.getDepositProgress(1L);

        assertThat(result.getAchievementRate()).isEqualByComparingTo("0.00");
        assertThat(result.getScore()).isEqualByComparingTo("0.00");
        assertThat(result.getRemainingDeposit()).isEqualTo(50_000_000L);
    }

    @Test
    @DisplayName("현재 보증금이 목표와 같으면 달성률 100%, 45점이고 부족 금액은 0원이다")
    void capsProgressAtTargetDeposit() {
        when(preparationMapper.findDepositProgressByUserId(1L))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 50_000_000L));

        DepositProgressResponse result = preparationService.getDepositProgress(1L);

        assertThat(result.getAchievementRate()).isEqualByComparingTo("100.00");
        assertThat(result.getScore()).isEqualByComparingTo("45.00");
        assertThat(result.getRemainingDeposit()).isZero();
    }

    @Test
    @DisplayName("현재 보증금이 목표를 초과해도 달성률 100%, 45점을 넘지 않는다")
    void capsProgressWhenCurrentDepositExceedsTarget() {
        when(preparationMapper.findDepositProgressByUserId(1L))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 60_000_000L));

        DepositProgressResponse result = preparationService.getDepositProgress(1L);

        assertThat(result.getCurrentDeposit()).isEqualTo(60_000_000L);
        assertThat(result.getAchievementRate()).isEqualByComparingTo("100.00");
        assertThat(result.getScore()).isEqualByComparingTo("45.00");
        assertThat(result.getRemainingDeposit()).isZero();
    }

    @Test
    @DisplayName("보증금 계산 데이터가 없으면 DEPOSIT_DATA_NOT_FOUND 예외를 던진다")
    void throwsWhenDepositProgressDoesNotExist() {
        when(preparationMapper.findDepositProgressByUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> preparationService.getDepositProgress(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.DEPOSIT_DATA_NOT_FOUND);
    }

    @Test
    @DisplayName("목표 보증금이 0 이하이면 DEPOSIT_NOT_CALCULABLE 예외를 던진다")
    void throwsWhenTargetDepositIsNotPositive() {
        when(preparationMapper.findDepositProgressByUserId(1L))
                .thenReturn(new DepositProgressSourceResponse(0L, 0L));

        assertThatThrownBy(() -> preparationService.getDepositProgress(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.DEPOSIT_NOT_CALCULABLE);
    }

    @Test
    @DisplayName("현재 보증금이 음수이면 DEPOSIT_NOT_CALCULABLE 예외를 던진다")
    void throwsWhenCurrentDepositIsNegative() {
        when(preparationMapper.findDepositProgressByUserId(1L))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, -1L));

        assertThatThrownBy(() -> preparationService.getDepositProgress(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.DEPOSIT_NOT_CALCULABLE);
    }
}

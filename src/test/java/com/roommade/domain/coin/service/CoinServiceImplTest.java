package com.roommade.domain.coin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.coin.code.CoinErrorCode;
import com.roommade.domain.coin.mapper.CoinMapper;
import com.roommade.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoinServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CoinMapper coinMapper;

    @InjectMocks
    private CoinServiceImpl coinService;

    @Test
    void returnsUserCoinBalance() {
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(120);

        assertThat(coinService.getBalance(USER_ID).getBalance()).isEqualTo(120);
    }

    @Test
    void returnsZeroWhenUserHasNoWalletYet() {
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(0);

        assertThat(coinService.getBalance(USER_ID).getBalance()).isZero();
    }

    @Test
    void earnsCoinAndReturnsUpdatedBalance() {
        when(coinMapper.updateBalanceForEarning(USER_ID, 50)).thenReturn(1);
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(150);

        int result = coinService.earn(USER_ID, 50);

        assertThat(result).isEqualTo(150);
        verify(coinMapper).insertWalletIfAbsent(USER_ID);
    }

    @Test
    void spendsCoinAndReturnsUpdatedBalance() {
        when(coinMapper.updateBalanceForSpending(USER_ID, 40)).thenReturn(1);
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(60);

        assertThat(coinService.spend(USER_ID, 40)).isEqualTo(60);
    }

    @Test
    void rejectsSpendingMoreThanBalance() {
        when(coinMapper.updateBalanceForSpending(USER_ID, 101)).thenReturn(0);
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(100);

        assertThatThrownBy(() -> coinService.spend(USER_ID, 101))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(CoinErrorCode.INSUFFICIENT_COIN_BALANCE);
    }

    @Test
    void rejectsNonPositiveAmountBeforeMapperCall() {
        assertThatThrownBy(() -> coinService.earn(USER_ID, 0))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(CoinErrorCode.INVALID_COIN_AMOUNT);

        verify(coinMapper, never()).insertWalletIfAbsent(USER_ID);
    }

    @Test
    void rejectsUnknownUser() {
        when(coinMapper.updateBalanceForSpending(USER_ID, 1)).thenReturn(0);
        when(coinMapper.findBalanceByUserId(USER_ID)).thenReturn(null);

        assertThatThrownBy(() -> coinService.spend(USER_ID, 1))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(CoinErrorCode.USER_NOT_FOUND);
    }
}

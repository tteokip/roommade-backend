package com.roommade.domain.coin.service;

import com.roommade.domain.coin.code.CoinErrorCode;
import com.roommade.domain.coin.dto.response.CoinBalanceResponse;
import com.roommade.domain.coin.mapper.CoinMapper;
import com.roommade.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinServiceImpl implements CoinService {

    private final CoinMapper coinMapper;

    @Override
    public CoinBalanceResponse getBalance(Long userId) {
        return new CoinBalanceResponse(findBalance(userId));
    }

    @Override
    @Transactional
    public int earn(Long userId, int amount) {
        validateAmount(amount);
        coinMapper.insertWalletIfAbsent(userId);
        if (coinMapper.updateBalanceForEarning(userId, amount) == 0) {
            throw new BusinessException(CoinErrorCode.USER_NOT_FOUND);
        }
        return findBalance(userId);
    }

    @Override
    @Transactional
    public int spend(Long userId, int amount) {
        validateAmount(amount);
        if (coinMapper.updateBalanceForSpending(userId, amount) == 0) {
            Integer balance = coinMapper.findBalanceByUserId(userId);
            if (balance == null) {
                throw new BusinessException(CoinErrorCode.USER_NOT_FOUND);
            }
            throw new BusinessException(CoinErrorCode.INSUFFICIENT_COIN_BALANCE);
        }
        return findBalance(userId);
    }

    private int findBalance(Long userId) {
        Integer balance = coinMapper.findBalanceByUserId(userId);
        if (balance == null) {
            throw new BusinessException(CoinErrorCode.USER_NOT_FOUND);
        }
        return balance;
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new BusinessException(CoinErrorCode.INVALID_COIN_AMOUNT);
        }
    }
}

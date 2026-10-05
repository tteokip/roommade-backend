package com.roommade.domain.coin.service;

import com.roommade.domain.coin.dto.response.CoinBalanceResponse;

public interface CoinService {

    CoinBalanceResponse getBalance(Long userId);

    int earn(Long userId, int amount);

    int spend(Long userId, int amount);
}

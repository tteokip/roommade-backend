package com.roommade.domain.coin.controller;

import com.roommade.domain.coin.code.CoinErrorCode;
import com.roommade.domain.coin.code.CoinSuccessCode;
import com.roommade.domain.coin.dto.response.CoinBalanceResponse;
import com.roommade.domain.coin.service.CoinService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coins")
public class CoinController {

    private final CoinService coinService;

    @GetMapping("/balance")
    public ApiResponse<CoinBalanceResponse> getBalance(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(CoinErrorCode.USER_ID_REQUIRED);
        }
        CoinBalanceResponse response = coinService.getBalance(userId);
        return ApiResponse.success(CoinSuccessCode.COIN_BALANCE_FOUND, response);
    }
}

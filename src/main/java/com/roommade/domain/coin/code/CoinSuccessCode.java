package com.roommade.domain.coin.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoinSuccessCode implements SuccessCode {

    COIN_BALANCE_FOUND(HttpStatus.OK, "COIN_001", "보유 코인을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

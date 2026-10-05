package com.roommade.domain.coin.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoinErrorCode implements ErrorCode {

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "COIN_002", "X-User-Id 헤더가 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COIN_003", "존재하지 않는 사용자입니다."),
    INVALID_COIN_AMOUNT(HttpStatus.BAD_REQUEST, "COIN_004", "코인 수량은 0보다 커야 합니다."),
    INSUFFICIENT_COIN_BALANCE(HttpStatus.UNPROCESSABLE_ENTITY, "COIN_005", "보유 코인이 부족합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

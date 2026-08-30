package com.roommade.domain.preparation.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PreparationErrorCode implements ErrorCode {

    RIR_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "PREPARATION_002", "RIR 계산에 필요한 사용자 정보를 찾을 수 없습니다."),
    RIR_NOT_CALCULABLE(HttpStatus.UNPROCESSABLE_ENTITY, "PREPARATION_003", "월 소득과 예상 월세는 0보다 커야 합니다."),
    DEPOSIT_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "PREPARATION_005", "보증금 계산에 필요한 사용자 정보를 찾을 수 없습니다."),
    DEPOSIT_NOT_CALCULABLE(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "PREPARATION_006",
            "목표 보증금은 0보다 크고 현재 보증금은 0 이상이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

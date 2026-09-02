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
            "목표 보증금은 0보다 크고 현재 보증금은 0 이상이어야 합니다."),
    INDEPENDENCE_PROGRESS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PREPARATION_009",
            "자립 준비 진행 정보를 찾을 수 없습니다."),
    MOVE_IN_ALREADY_CONFIRMED(
            HttpStatus.CONFLICT,
            "PREPARATION_010",
            "이미 입주를 확정했습니다."),
    INVALID_MOVE_IN_CONFIRMATION(
            HttpStatus.BAD_REQUEST,
            "PREPARATION_012",
            "입주 확정 유형과 매물 정보가 올바르지 않습니다."),
    MOVE_IN_DATE_IN_PAST(
            HttpStatus.BAD_REQUEST,
            "PREPARATION_013",
            "입주일은 오늘 또는 미래여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

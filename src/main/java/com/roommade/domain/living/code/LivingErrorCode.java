package com.roommade.domain.living.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LivingErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "LIVING_003", "존재하지 않는 사용자입니다."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "LIVING_004", "X-User-Id 헤더가 필요합니다."),
    EMERGENCY_FUND_NOT_SET(HttpStatus.NOT_FOUND, "LIVING_005", "아직 비상금 목표가 설정되지 않았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

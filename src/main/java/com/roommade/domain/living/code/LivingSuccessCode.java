package com.roommade.domain.living.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LivingSuccessCode implements SuccessCode {

    EMERGENCY_FUND_FOUND(HttpStatus.OK, "LIVING_001", "비상금 현황을 조회했습니다."),
    EMERGENCY_FUND_TARGET_UPDATED(HttpStatus.OK, "LIVING_002", "비상금 목표 금액을 설정했습니다."),
    EMERGENCY_FUND_CURRENT_AMOUNT_UPDATED(HttpStatus.OK, "LIVING_006", "현재 비상금 금액을 갱신했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
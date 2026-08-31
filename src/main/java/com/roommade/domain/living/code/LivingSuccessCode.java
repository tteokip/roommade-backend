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
    DAILY_LIVING_COST_FOUND(HttpStatus.OK, "LIVING_005", "생활비 현황을 조회했습니다."),
    MONTHLY_LIVING_COST_FOUND(HttpStatus.OK, "LIVING_006", "월별 생활비 현황을 조회했습니다."),
    DAILY_CHALLENGE_FOUND(HttpStatus.OK, "LIVING_007", "일간 챌린지 현황을 조회했습니다."),
    LIVING_RENT_UPDATED(HttpStatus.OK, "LIVING_008", "월세 정보를 저장했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
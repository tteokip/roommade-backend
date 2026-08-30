package com.roommade.domain.preparation.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PreparationSuccessCode implements SuccessCode {

    RIR_DIAGNOSIS_FOUND(HttpStatus.OK, "PREPARATION_001", "RIR 진단 결과를 조회했습니다."),
    DEPOSIT_PROGRESS_FOUND(HttpStatus.OK, "PREPARATION_004", "보증금 마련 현황을 조회했습니다."),
    HOUSE_COMPARISON_PROGRESS_FOUND(
            HttpStatus.OK,
            "PREPARATION_007",
            "집 비교 점수를 조회했습니다."),
    READINESS_DIAGNOSIS_FOUND(
            HttpStatus.OK,
            "PREPARATION_008",
            "자립 준비도를 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

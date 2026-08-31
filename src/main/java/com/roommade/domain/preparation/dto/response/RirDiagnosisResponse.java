package com.roommade.domain.preparation.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 금액 필드는 원 단위로 반환한다. */
@Getter
@AllArgsConstructor
public class RirDiagnosisResponse {

    public enum Status {
        NORMAL, // RIR 30% 이하
        EXCESSIVE,  // RIR 30% 초과 50% 미만
        SEVERE  // RIR 50% 이상
    }

    private Long monthlyIncome;
    private Long expectedMonthlyRent;
    private BigDecimal rirPercent;
    private BigDecimal achievementRate;
    private BigDecimal score;
    private Integer maxScore;
    private Integer targetRirPercent;
    private Status status;
    private Long targetMonthlyRent;
    private Long requiredRentReduction;
}

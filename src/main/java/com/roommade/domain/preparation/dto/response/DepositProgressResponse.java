package com.roommade.domain.preparation.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 금액 필드는 원 단위로 반환한다. */
@Getter
@AllArgsConstructor
public class DepositProgressResponse {

    private Long targetDeposit;
    private Long currentDeposit;
    private BigDecimal achievementRate;
    private BigDecimal score;
    private Integer maxScore;
    private Long remainingDeposit;
}

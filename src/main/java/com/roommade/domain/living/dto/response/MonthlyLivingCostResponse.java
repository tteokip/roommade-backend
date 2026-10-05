package com.roommade.domain.living.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyLivingCostResponse {

    private Long lastMonthTotal;
    private Long previousMonthTotal;
    private Long differenceAmount;
    private BigDecimal differenceRate;
}

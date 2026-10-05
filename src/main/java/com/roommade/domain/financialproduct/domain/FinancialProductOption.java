package com.roommade.domain.financialproduct.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialProductOption {
    private Long productId;
    private String interestRateType;
    private String reserveType;
    private Integer saveTerm;
    private BigDecimal baseInterestRate;
    private BigDecimal maxInterestRate;
}

package com.roommade.domain.financialproduct.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FinancialProductOptionResponse {
    private Long optionId;
    private String interestRateType;
    private String reserveType;
    private Integer saveTerm;
    private BigDecimal baseInterestRate;
    private BigDecimal maxInterestRate;
}

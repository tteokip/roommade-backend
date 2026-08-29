package com.roommade.domain.financialproduct.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FinancialProductSummaryResponse {
    private Long productId;
    private String institutionName;
    private String productName;
    private BigDecimal maxInterestRate;
    private Long maxLimit;
    private String joinMethod;
}

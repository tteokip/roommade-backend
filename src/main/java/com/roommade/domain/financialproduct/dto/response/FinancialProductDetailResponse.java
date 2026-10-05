package com.roommade.domain.financialproduct.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FinancialProductDetailResponse {
    private Long productId;
    private String institutionName;
    private String productName;
    private String joinMethod;
    private String joinTarget;
    private String joinRestriction;
    private String specialCondition;
    private String maturityInterest;
    private Long maxLimit;
    private String notice;
    private String disclosureMonth;
    private LocalDate disclosureStartDate;
    private LocalDate disclosureEndDate;
    private String productPageUrl;
    private List<FinancialProductOptionResponse> options;
}

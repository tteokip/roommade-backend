package com.roommade.domain.financialproduct.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInstitution {
    private Long financialInstitutionId;
    private String financialInstitutionCode;
    private String financialInstitutionName;
}

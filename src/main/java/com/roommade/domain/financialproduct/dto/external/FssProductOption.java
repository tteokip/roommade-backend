package com.roommade.domain.financialproduct.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FssProductOption {
    @JsonProperty("fin_co_no") private String financialCompanyCode;
    @JsonProperty("fin_prdt_cd") private String financialProductCode;
    @JsonProperty("intr_rate_type") private String interestRateType;
    @JsonProperty("rsrv_type") private String reserveType;
    @JsonProperty("save_trm") private String saveTerm;
    @JsonProperty("intr_rate") private BigDecimal interestRate;
    @JsonProperty("intr_rate2") private BigDecimal maximumInterestRate;
}

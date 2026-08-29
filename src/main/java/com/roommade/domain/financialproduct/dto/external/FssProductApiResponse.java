package com.roommade.domain.financialproduct.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FssProductApiResponse {
    @JsonProperty("result")
    private FssProductResult result;
}

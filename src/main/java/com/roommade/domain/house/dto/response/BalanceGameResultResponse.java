package com.roommade.domain.house.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameResultResponse {

    private String result;
    private int houseAScore;
    private int houseBScore;
    private Map<ComparisonFactor, Integer> selectedFactors;
    private Map<ComparisonFactor, Integer> matchedFactors;
    private List<ComparisonFactor> excludedFactors;
}

package com.roommade.domain.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseComparisonCurrentResponse {

    private Long comparisonId;
    private HouseResponse houseA;
    private HouseResponse houseB;
    private boolean balanceGameAvailable;

    public static HouseComparisonCurrentResponse noComparison() {
        return new HouseComparisonCurrentResponse(null, null, null, false);
    }
}

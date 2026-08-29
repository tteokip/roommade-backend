package com.roommade.domain.house.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseAnalysisResultResponse {

    private String location;
    private Long deposit;
    private Long monthlyRent;
    private Long maintenanceFee;
    private BigDecimal area;
    private Integer stationWalkMinutes;
    private Integer commuteMinutes;
    private String floorType;
    private String roomStructure;
    private String optionType;
}

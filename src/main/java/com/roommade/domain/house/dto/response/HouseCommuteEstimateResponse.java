package com.roommade.domain.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseCommuteEstimateResponse {

    private String location;
    private Integer commuteMinMinutes;
    private Integer commuteMaxMinutes;
}

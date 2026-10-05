package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.response.HouseCommuteEstimateResponse;

public interface HouseCommuteService {

    HouseCommuteEstimateResponse estimate(Long userId, String location);
}

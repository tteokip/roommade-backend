package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;

public interface HouseComparisonService {

    HouseComparisonCurrentResponse getCurrentComparison(Long userId);
}

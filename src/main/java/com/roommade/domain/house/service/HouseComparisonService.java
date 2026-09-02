package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;

public interface HouseComparisonService {

    HouseComparisonCurrentResponse getCurrentComparison(Long userId);

    HouseComparisonCurrentResponse registerHouse(Long userId, String houseType, HouseRegisterRequest request);

    /** 매물이 사용자의 비교 대상 매물인지 확인. */
    boolean isComparisonHouseOwnedByUser(Long userId, Long houseId);
}

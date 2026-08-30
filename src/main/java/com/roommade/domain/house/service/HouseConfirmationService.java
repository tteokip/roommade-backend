package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.request.HouseConfirmRequest;
import com.roommade.domain.house.dto.response.HouseConfirmationResponse;

public interface HouseConfirmationService {

    HouseConfirmationResponse confirmHouse(Long userId, HouseConfirmRequest request);
}

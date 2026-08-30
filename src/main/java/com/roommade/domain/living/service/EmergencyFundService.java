package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.EmergencyFundResponse;

public interface EmergencyFundService {

    EmergencyFundResponse getEmergencyFund(Long userId);

    EmergencyFundResponse setTarget(Long userId, Long targetAmount);

    EmergencyFundResponse updateCurrentAmount(Long userId, Long currentAmount);
}
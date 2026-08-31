package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.LivingRentResponse;

public interface LivingRentService {

    LivingRentResponse setMonthlyRent(Long userId, Long monthlyRent);
}

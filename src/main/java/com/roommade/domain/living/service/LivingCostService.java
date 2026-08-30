package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.DailyLivingCostOverviewResponse;
import com.roommade.domain.living.dto.response.MonthlyLivingCostResponse;

public interface LivingCostService {

    DailyLivingCostOverviewResponse getDailyLivingCostOverview(Long userId);

    MonthlyLivingCostResponse getMonthlyLivingCost(Long userId);
}

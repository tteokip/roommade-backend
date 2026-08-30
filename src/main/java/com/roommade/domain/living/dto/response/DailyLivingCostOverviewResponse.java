package com.roommade.domain.living.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyLivingCostOverviewResponse {

    private Long thisMonthTotal;
    private Long sameDayLastMonthTotal;
    private Long differenceFromLastMonth;
    private List<DailyLivingCostItemResponse> dailyBreakdown;
}

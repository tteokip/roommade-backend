package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.DailyLivingCostItemResponse;
import com.roommade.domain.living.dto.response.DailyLivingCostOverviewResponse;
import com.roommade.domain.living.dto.response.MonthlyLivingCostResponse;
import com.roommade.domain.living.mapper.LivingCostMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LivingCostServiceImpl implements LivingCostService {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final LivingCostMapper livingCostMapper;

    @Override
    public DailyLivingCostOverviewResponse getDailyLivingCostOverview(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate thisMonthStart = today.withDayOfMonth(1);

        LocalDate lastMonthSameDay = today.minusMonths(1);
        LocalDate lastMonthStart = lastMonthSameDay.withDayOfMonth(1);
        LocalDate lastMonthEnd = lastMonthSameDay.withDayOfMonth(
                Math.min(today.getDayOfMonth(), lastMonthSameDay.lengthOfMonth()));
        LocalDate lastMonthFullEnd = YearMonth.from(lastMonthStart).atEndOfMonth();

        List<DailyLivingCostItemResponse> dailyBreakdown =
                livingCostMapper.findDailyCostsBetween(userId, thisMonthStart, today);
        List<DailyLivingCostItemResponse> lastMonthDailyBreakdown =
                livingCostMapper.findDailyCostsBetween(userId, lastMonthStart, lastMonthFullEnd);
        Long thisMonthTotal = livingCostMapper.sumDailyCostsBetween(userId, thisMonthStart, today);
        Long sameDayLastMonthTotal =
                livingCostMapper.sumDailyCostsBetween(userId, lastMonthStart, lastMonthEnd);

        return new DailyLivingCostOverviewResponse(
                thisMonthTotal,
                sameDayLastMonthTotal,
                thisMonthTotal - sameDayLastMonthTotal,
                dailyBreakdown,
                lastMonthDailyBreakdown);
    }

    @Override
    public MonthlyLivingCostResponse getMonthlyLivingCost(Long userId) {
        YearMonth lastMonth = YearMonth.from(LocalDate.now().minusMonths(1));
        YearMonth previousMonth = lastMonth.minusMonths(1);

        Long lastMonthTotal = nullToZero(
                livingCostMapper.findMonthlyTotal(userId, lastMonth.format(YEAR_MONTH_FORMAT)));
        Long previousMonthTotal = nullToZero(
                livingCostMapper.findMonthlyTotal(userId, previousMonth.format(YEAR_MONTH_FORMAT)));

        long differenceAmount = lastMonthTotal - previousMonthTotal;
        BigDecimal differenceRate = previousMonthTotal == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(differenceAmount)
                        .multiply(ONE_HUNDRED)
                        .divide(BigDecimal.valueOf(previousMonthTotal), 2, RoundingMode.HALF_UP);

        return new MonthlyLivingCostResponse(lastMonthTotal, previousMonthTotal, differenceAmount, differenceRate);
    }

    private Long nullToZero(Long value) {
        return value != null ? value : 0L;
    }
}

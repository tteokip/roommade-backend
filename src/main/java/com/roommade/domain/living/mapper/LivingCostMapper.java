package com.roommade.domain.living.mapper;

import com.roommade.domain.living.dto.response.DailyLivingCostItemResponse;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LivingCostMapper {

    List<DailyLivingCostItemResponse> findDailyCostsBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Long sumDailyCostsBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Long findMonthlyTotal(
            @Param("userId") Long userId,
            @Param("yearMonth") String yearMonth);
}

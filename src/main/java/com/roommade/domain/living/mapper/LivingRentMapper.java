package com.roommade.domain.living.mapper;

import com.roommade.domain.living.dto.response.LivingRentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LivingRentMapper {

    LivingRentResponse findByUserId(@Param("userId") Long userId);

    void insert(@Param("userId") Long userId, @Param("monthlyRent") Long monthlyRent);

    void updateRent(@Param("userId") Long userId, @Param("monthlyRent") Long monthlyRent);

    Long findMonthlyIncomeByUserId(@Param("userId") Long userId);
}

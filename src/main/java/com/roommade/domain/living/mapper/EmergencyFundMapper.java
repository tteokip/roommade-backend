package com.roommade.domain.living.mapper;

import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmergencyFundMapper {

    EmergencyFundResponse findByUserId(@Param("userId") Long userId);

    void insert(
            @Param("userId") Long userId,
            @Param("targetAmount") Long targetAmount,
            @Param("achievedAt") LocalDateTime achievedAt);

    void updateTarget(
            @Param("userId") Long userId,
            @Param("targetAmount") Long targetAmount,
            @Param("achievedAt") LocalDateTime achievedAt);

    void updateCurrentAmount(
            @Param("userId") Long userId,
            @Param("currentAmount") Long currentAmount,
            @Param("achievedAt") LocalDateTime achievedAt);
}
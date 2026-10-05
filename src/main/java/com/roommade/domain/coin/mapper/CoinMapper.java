package com.roommade.domain.coin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoinMapper {

    Integer findBalanceByUserId(@Param("userId") Long userId);

    int insertWalletIfAbsent(@Param("userId") Long userId);

    int updateBalanceForEarning(
            @Param("userId") Long userId,
            @Param("amount") int amount);

    int updateBalanceForSpending(
            @Param("userId") Long userId,
            @Param("amount") int amount);
}

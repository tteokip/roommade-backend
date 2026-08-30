package com.roommade.domain.preparation.mapper;

import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreparationMapper {

    /** 사용자 RIR 계산용 월 소득과 월세 상한 조회. */
    RirProfileResponse findRirProfileByUserId(@Param("userId") Long userId);

    /** 사용자 보증금 계산용 목표 금액과 현재 마련 금액 조회. */
    DepositProgressSourceResponse findDepositProgressByUserId(@Param("userId") Long userId);
}

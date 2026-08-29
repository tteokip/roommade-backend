package com.roommade.domain.preparation.mapper;

import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreparationMapper {

    /** 사용자 RIR 계산용 월 소득과 월세 상한 조회. */
    RirProfileResponse findRirProfileByUserId(@Param("userId") Long userId);
}

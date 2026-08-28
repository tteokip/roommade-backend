package com.roommade.domain.house.mapper;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseComparisonMapper {

    HouseComparisonCurrentResponse findCurrentByUserId(@Param("userId") Long userId);
}

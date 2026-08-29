package com.roommade.domain.house.mapper;

import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseComparisonMapper {

    HouseComparisonCurrentResponse findCurrentByUserId(@Param("userId") Long userId);

    void insertComparison(@Param("userId") Long userId);

    void insertHouse(
            @Param("comparisonId") Long comparisonId,
            @Param("houseType") String houseType,
            @Param("request") HouseRegisterRequest request);
}

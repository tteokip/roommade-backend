package com.roommade.domain.house.mapper;

import com.roommade.domain.house.dto.response.WorkplaceAddressResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseCommuteMapper {

    /** 사용자 직장 주소 조회. user_profiles에 사용자 행 자체가 없으면 null을 반환한다. */
    WorkplaceAddressResponse findWorkplaceAddressByUserId(@Param("userId") Long userId);
}

package com.roommade.domain.user.mapper;

import com.roommade.domain.user.dto.response.UserPolicyProfileResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    UserPolicyProfileResponse findPolicyProfileByUserId(@Param("userId") Long userId);
}

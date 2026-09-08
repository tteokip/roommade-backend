package com.roommade.domain.user.mapper;

import com.roommade.domain.user.dto.request.UserSignupRequest;
import com.roommade.domain.user.dto.response.UserLoginSourceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    boolean existsByEmail(@Param("email") String email);

    int insertUser(@Param("email") String email, @Param("passwordHash") String passwordHash);

    int insertProfile(@Param("userId") Long userId, @Param("request") UserSignupRequest request);

    int insertCoinWallet(@Param("userId") Long userId);

    int insertIndependenceProgress(@Param("userId") Long userId);

    Long findIdByEmail(@Param("email") String email);

    UserLoginSourceResponse findLoginUserByEmail(@Param("email") String email);
}

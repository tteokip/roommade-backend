package com.roommade.domain.user.service;

import com.roommade.domain.user.dto.request.UserLoginRequest;
import com.roommade.domain.user.dto.request.UserSignupRequest;
import com.roommade.domain.user.dto.response.UserLoginResponse;
import com.roommade.domain.user.dto.response.UserSignupResponse;

public interface UserService {

    UserSignupResponse signup(UserSignupRequest request);

    UserLoginResponse login(UserLoginRequest request);
}

package com.roommade.domain.user.controller;

import com.roommade.domain.user.code.UserSuccessCode;
import com.roommade.domain.user.dto.request.UserSignupRequest;
import com.roommade.domain.user.dto.request.UserLoginRequest;
import com.roommade.domain.user.dto.response.UserLoginResponse;
import com.roommade.domain.user.dto.response.UserSignupResponse;
import com.roommade.domain.user.service.UserService;
import com.roommade.global.response.ApiResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(
            @Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);
        return ResponseEntity.status(UserSuccessCode.USER_SIGNED_UP.getStatus())
                .body(ApiResponse.success(UserSuccessCode.USER_SIGNED_UP, response));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {
        UserLoginResponse response = userService.login(request);
        httpRequest.getSession().setAttribute("userId", response.getUserId());
        return ApiResponse.success(UserSuccessCode.USER_LOGGED_IN, response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success(UserSuccessCode.USER_LOGGED_OUT, null);
    }
}

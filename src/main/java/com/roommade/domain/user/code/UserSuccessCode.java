package com.roommade.domain.user.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements SuccessCode {

    USER_SIGNED_UP(HttpStatus.CREATED, "USER_001", "회원가입이 완료되었습니다."),
    USER_LOGGED_IN(HttpStatus.OK, "USER_002", "로그인되었습니다."),
    USER_LOGGED_OUT(HttpStatus.OK, "USER_003", "로그아웃되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

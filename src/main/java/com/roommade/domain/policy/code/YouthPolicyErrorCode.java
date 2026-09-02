package com.roommade.domain.policy.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum YouthPolicyErrorCode implements ErrorCode {
    YOUTH_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "YOUTH_POLICY_003", "청년 정책을 찾을 수 없습니다."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "YOUTH_POLICY_004", "X-User-Id 헤더가 필요합니다."),
    USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "YOUTH_POLICY_005", "사용자 프로필을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

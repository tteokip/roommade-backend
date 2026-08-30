package com.roommade.domain.policy.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum YouthPolicyErrorCode implements ErrorCode {
    YOUTH_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "YOUTH_POLICY_003", "청년 정책을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

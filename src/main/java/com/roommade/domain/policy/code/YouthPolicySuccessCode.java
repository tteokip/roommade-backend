package com.roommade.domain.policy.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum YouthPolicySuccessCode implements SuccessCode {
    YOUTH_POLICIES_SYNCED(HttpStatus.OK, "YOUTH_POLICY_000", "청년 정책을 동기화했습니다."),
    YOUTH_POLICIES_RETRIEVED(HttpStatus.OK, "YOUTH_POLICY_001", "청년 정책 목록을 조회했습니다."),
    YOUTH_POLICY_RETRIEVED(HttpStatus.OK, "YOUTH_POLICY_002", "청년 정책 상세 정보를 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

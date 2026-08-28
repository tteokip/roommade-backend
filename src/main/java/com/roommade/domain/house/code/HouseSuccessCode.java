package com.roommade.domain.house.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HouseSuccessCode implements SuccessCode {

    HOUSE_COMPARISON_CURRENT_FOUND(HttpStatus.OK, "HOUSE_001", "현재 집 비교를 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

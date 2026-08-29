package com.roommade.domain.house.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HouseErrorCode implements ErrorCode {

    INVALID_HOUSE_TYPE(HttpStatus.BAD_REQUEST, "HOUSE_003", "houseType은 A 또는 B여야 합니다."),
    HOUSE_SLOT_ALREADY_OCCUPIED(HttpStatus.CONFLICT, "HOUSE_004", "이미 등록된 매물 슬롯입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

package com.roommade.domain.room.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements ErrorCode {

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "ROOM_005", "X-User-Id 헤더가 필요합니다."),
    FURNITURE_REWARD_NOT_AVAILABLE(
            HttpStatus.NOT_FOUND, "ROOM_006", "사용 가능한 가구 선택권이 없습니다."),
    FURNITURE_NOT_SELECTABLE(
            HttpStatus.UNPROCESSABLE_ENTITY, "ROOM_007", "해당 선택권으로 선택할 수 없는 가구입니다."),
    FURNITURE_NOT_OWNED(
            HttpStatus.NOT_FOUND, "ROOM_008", "보유하지 않은 가구입니다."),
    FURNITURE_NOT_PURCHASABLE(
            HttpStatus.NOT_FOUND, "ROOM_010", "구매할 수 없는 가구입니다."),
    FURNITURE_ALREADY_OWNED(
            HttpStatus.CONFLICT, "ROOM_011", "이미 보유한 가구입니다."),
    FURNITURE_CATEGORY_NOT_UNLOCKED(
            HttpStatus.UNPROCESSABLE_ENTITY, "ROOM_013", "해당 가구 카테고리가 해금되지 않았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

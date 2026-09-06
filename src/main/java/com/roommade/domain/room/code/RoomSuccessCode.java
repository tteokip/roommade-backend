package com.roommade.domain.room.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoomSuccessCode implements SuccessCode {

    ROOM_FOUND(HttpStatus.OK, "ROOM_001", "내 방을 조회했습니다."),
    FURNITURE_REWARDS_FOUND(HttpStatus.OK, "ROOM_002", "가구 선택권을 조회했습니다."),
    FURNITURE_REWARD_CLAIMED(HttpStatus.OK, "ROOM_003", "가구를 획득했습니다."),
    FURNITURE_PLACEMENT_UPDATED(HttpStatus.OK, "ROOM_004", "가구 배치 상태를 변경했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

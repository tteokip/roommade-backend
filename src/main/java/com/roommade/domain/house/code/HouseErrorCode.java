package com.roommade.domain.house.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HouseErrorCode implements ErrorCode {

    INVALID_HOUSE_TYPE(HttpStatus.BAD_REQUEST, "HOUSE_003", "houseType은 A 또는 B여야 합니다."),
    HOUSE_SLOT_ALREADY_OCCUPIED(HttpStatus.CONFLICT, "HOUSE_004", "이미 등록된 매물 슬롯입니다."),
    INVALID_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "HOUSE_006", "이미지는 1장 이상 3장 이하로 첨부해야 합니다."),
    EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "HOUSE_007", "빈 이미지 파일은 첨부할 수 없습니다."),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "HOUSE_008", "지원하지 않는 이미지 형식입니다."),
    HOUSE_ANALYSIS_FAILED(HttpStatus.BAD_GATEWAY, "HOUSE_009", "매물 이미지 분석에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

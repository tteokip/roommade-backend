package com.roommade.domain.house.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HouseSuccessCode implements SuccessCode {

    HOUSE_COMPARISON_CURRENT_FOUND(HttpStatus.OK, "HOUSE_001", "현재 집 비교를 조회했습니다."),
    HOUSE_REGISTERED(HttpStatus.CREATED, "HOUSE_002", "매물을 등록했습니다."),
    HOUSE_ANALYZED(HttpStatus.OK, "HOUSE_005", "매물 이미지 분석에 성공했습니다."),
    BALANCE_GAME_QUESTIONS_FOUND(HttpStatus.OK, "HOUSE_010", "밸런스게임 질문을 조회했습니다."),
    BALANCE_GAME_ANSWER_SAVED(HttpStatus.OK, "HOUSE_011", "밸런스게임 답변을 저장했습니다."),
    BALANCE_GAME_RESULT_FOUND(HttpStatus.OK, "HOUSE_012", "밸런스게임 결과를 조회했습니다."),
    HOUSE_CONFIRMED(HttpStatus.CREATED, "HOUSE_017", "집을 확정했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

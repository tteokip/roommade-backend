package com.roommade.domain.quiz.code;

import com.roommade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuizErrorCode implements ErrorCode {

    ACTIVE_QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_004", "출제 가능한 퀴즈가 없습니다."),
    QUIZ_ALREADY_ATTEMPTED(HttpStatus.CONFLICT, "QUIZ_005", "오늘의 퀴즈에 이미 참여했습니다."),
    INVALID_QUIZ_CHOICE(HttpStatus.BAD_REQUEST, "QUIZ_006", "오늘의 퀴즈에 해당하지 않는 선택지입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

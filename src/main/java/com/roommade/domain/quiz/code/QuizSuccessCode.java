package com.roommade.domain.quiz.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuizSuccessCode implements SuccessCode {

    TODAY_QUIZ_RETRIEVED(HttpStatus.OK, "QUIZ_001", "오늘의 퀴즈를 조회했습니다."),
    QUIZ_ANSWER_SUBMITTED(HttpStatus.OK, "QUIZ_002", "퀴즈 답안을 제출했습니다."),
    QUIZ_HISTORY_RETRIEVED(HttpStatus.OK, "QUIZ_003", "퀴즈 기록을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

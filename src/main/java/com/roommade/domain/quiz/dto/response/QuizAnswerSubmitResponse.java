package com.roommade.domain.quiz.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerSubmitResponse {

    private LocalDate quizDate;
    private boolean correct;
    private String correctChoiceContent;
    private String explanation;
    private int earnedPoint;
    private int currentStreak;
    private int coinBalance;
}

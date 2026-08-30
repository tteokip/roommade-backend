package com.roommade.domain.quiz.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptHistoryResponse {

    private LocalDate quizDate;
    private Long quizQuestionId;
    private String question;
    private boolean correct;
    private int earnedPoint;
}

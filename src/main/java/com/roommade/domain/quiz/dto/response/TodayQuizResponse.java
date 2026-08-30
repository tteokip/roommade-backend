package com.roommade.domain.quiz.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodayQuizResponse {

    private LocalDate quizDate;
    private Long quizQuestionId;
    private String quizType;
    private String question;
    private List<QuizChoiceResponse> choices;
    private boolean attempted;
}

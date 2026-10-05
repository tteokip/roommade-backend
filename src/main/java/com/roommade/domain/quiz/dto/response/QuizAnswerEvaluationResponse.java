package com.roommade.domain.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerEvaluationResponse {

    private boolean correct;
    private String correctChoiceContent;
    private String explanation;
}

package com.roommade.domain.quiz.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerSubmitRequest {

    @NotNull(message = "selectedChoiceId는 필수입니다.")
    private Long selectedChoiceId;
}

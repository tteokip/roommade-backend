package com.roommade.domain.house.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameQuestionsResponse {

    private int totalQuestions;
    private int answeredQuestions;
    private boolean completed;
    private List<BalanceGameQuestionResponse> questions;
}

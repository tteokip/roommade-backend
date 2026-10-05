package com.roommade.domain.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameProgressResponse {

    private int totalQuestions;
    private int answeredQuestions;
    private boolean completed;
}

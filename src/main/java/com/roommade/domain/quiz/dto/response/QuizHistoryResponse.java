package com.roommade.domain.quiz.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizHistoryResponse {

    private int currentStreak;
    private List<QuizAttemptHistoryResponse> attempts;
}

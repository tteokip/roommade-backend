package com.roommade.domain.quiz.service;

import com.roommade.domain.quiz.dto.response.QuizAnswerSubmitResponse;
import com.roommade.domain.quiz.dto.response.QuizHistoryResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;

public interface QuizService {

    TodayQuizResponse getTodayQuiz(Long userId);

    QuizAnswerSubmitResponse submitTodayQuizAnswer(Long userId, Long selectedChoiceId);

    QuizHistoryResponse getQuizHistory(Long userId);
}

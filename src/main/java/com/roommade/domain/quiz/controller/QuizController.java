package com.roommade.domain.quiz.controller;

import com.roommade.domain.quiz.code.QuizSuccessCode;
import com.roommade.domain.quiz.dto.request.QuizAnswerSubmitRequest;
import com.roommade.domain.quiz.dto.response.QuizAnswerSubmitResponse;
import com.roommade.domain.quiz.dto.response.QuizHistoryResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;
import com.roommade.domain.quiz.service.QuizService;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/today")
    public ApiResponse<TodayQuizResponse> getTodayQuiz(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(QuizSuccessCode.TODAY_QUIZ_RETRIEVED, quizService.getTodayQuiz(userId));
    }

    @PostMapping("/today/attempts")
    public ApiResponse<QuizAnswerSubmitResponse> submitTodayQuizAnswer(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QuizAnswerSubmitRequest request) {
        return ApiResponse.success(
                QuizSuccessCode.QUIZ_ANSWER_SUBMITTED,
                quizService.submitTodayQuizAnswer(userId, request.getSelectedChoiceId()));
    }

    @GetMapping("/history")
    public ApiResponse<QuizHistoryResponse> getQuizHistory(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(QuizSuccessCode.QUIZ_HISTORY_RETRIEVED, quizService.getQuizHistory(userId));
    }
}

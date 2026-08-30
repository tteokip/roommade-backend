package com.roommade.domain.quiz.mapper;

import com.roommade.domain.quiz.dto.response.QuizAnswerEvaluationResponse;
import com.roommade.domain.quiz.dto.response.QuizAttemptHistoryResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizMapper {

    void insertDailyQuizIfAbsent(@Param("quizDate") LocalDate quizDate);

    TodayQuizResponse findTodayQuiz(@Param("quizDate") LocalDate quizDate, @Param("userId") Long userId);

    boolean existsAttemptByUserIdAndQuizDate(@Param("userId") Long userId, @Param("quizDate") LocalDate quizDate);

    QuizAnswerEvaluationResponse findAnswerEvaluation(
            @Param("quizQuestionId") Long quizQuestionId, @Param("selectedChoiceId") Long selectedChoiceId);

    void insertAttempt(
            @Param("userId") Long userId,
            @Param("quizDate") LocalDate quizDate,
            @Param("quizQuestionId") Long quizQuestionId,
            @Param("selectedChoiceId") Long selectedChoiceId,
            @Param("correct") boolean correct);

    int increaseCoinBalance(@Param("userId") Long userId, @Param("amount") int amount);

    void insertCoinWallet(@Param("userId") Long userId, @Param("amount") int amount);

    Integer findCoinBalanceByUserId(@Param("userId") Long userId);

    List<QuizAttemptHistoryResponse> findAttemptHistoryByUserId(@Param("userId") Long userId);
}

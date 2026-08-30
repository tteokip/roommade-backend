package com.roommade.domain.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.coin.dto.response.CoinBalanceResponse;
import com.roommade.domain.coin.service.CoinService;
import com.roommade.domain.quiz.dto.response.QuizAnswerEvaluationResponse;
import com.roommade.domain.quiz.dto.response.QuizAttemptHistoryResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;
import com.roommade.domain.quiz.mapper.QuizMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long QUIZ_QUESTION_ID = 10L;
    private static final Long CHOICE_ID = 100L;

    @Mock
    private QuizMapper quizMapper;

    @Mock
    private CoinService coinService;

    private QuizServiceImpl quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizServiceImpl(quizMapper, coinService);
    }

    @Test
    void submitTodayQuizAnswer_correctAnswerRewardsFiftyPoints() {
        LocalDate today = LocalDate.now();
        when(quizMapper.findTodayQuiz(any(), anyLong()))
                .thenReturn(new TodayQuizResponse(today, QUIZ_QUESTION_ID, "OX", "문제", List.of(), false));
        when(quizMapper.existsAttemptByUserIdAndQuizDate(anyLong(), any())).thenReturn(false);
        when(quizMapper.findAnswerEvaluation(QUIZ_QUESTION_ID, CHOICE_ID))
                .thenReturn(new QuizAnswerEvaluationResponse(true, "O", "해설"));
        when(coinService.earn(USER_ID, 50)).thenReturn(150);
        when(quizMapper.findAttemptHistoryByUserId(USER_ID))
                .thenReturn(List.of(new QuizAttemptHistoryResponse(today, QUIZ_QUESTION_ID, "문제", true, 50)));

        var response = quizService.submitTodayQuizAnswer(USER_ID, CHOICE_ID);

        assertThat(response.isCorrect()).isTrue();
        assertThat(response.getEarnedPoint()).isEqualTo(50);
        assertThat(response.getCurrentStreak()).isEqualTo(1);
        assertThat(response.getCoinBalance()).isEqualTo(150);
        verify(quizMapper).insertAttempt(USER_ID, today, QUIZ_QUESTION_ID, CHOICE_ID, true);
        verify(coinService).earn(USER_ID, 50);
    }

    @Test
    void submitTodayQuizAnswer_incorrectAnswerKeepsCurrentCoinBalance() {
        LocalDate today = LocalDate.now();
        when(quizMapper.findTodayQuiz(any(), anyLong()))
                .thenReturn(new TodayQuizResponse(today, QUIZ_QUESTION_ID, "OX", "문제", List.of(), false));
        when(quizMapper.existsAttemptByUserIdAndQuizDate(anyLong(), any())).thenReturn(false);
        when(quizMapper.findAnswerEvaluation(QUIZ_QUESTION_ID, CHOICE_ID))
                .thenReturn(new QuizAnswerEvaluationResponse(false, "X", "해설"));
        when(coinService.getBalance(USER_ID)).thenReturn(new CoinBalanceResponse(100));
        when(quizMapper.findAttemptHistoryByUserId(USER_ID))
                .thenReturn(List.of(new QuizAttemptHistoryResponse(today, QUIZ_QUESTION_ID, "문제", false, 0)));

        var response = quizService.submitTodayQuizAnswer(USER_ID, CHOICE_ID);

        assertThat(response.isCorrect()).isFalse();
        assertThat(response.getEarnedPoint()).isZero();
        assertThat(response.getCoinBalance()).isEqualTo(100);
        verify(coinService, never()).earn(anyLong(), anyInt());
        verify(coinService).getBalance(USER_ID);
    }

    @Test
    void submitTodayQuizAnswer_alreadyAttempted_throwsBusinessException() {
        LocalDate today = LocalDate.now();
        when(quizMapper.findTodayQuiz(any(), anyLong()))
                .thenReturn(new TodayQuizResponse(today, QUIZ_QUESTION_ID, "OX", "문제", List.of(), true));
        when(quizMapper.existsAttemptByUserIdAndQuizDate(anyLong(), any())).thenReturn(true);

        assertThatThrownBy(() -> quizService.submitTodayQuizAnswer(USER_ID, CHOICE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("오늘의 퀴즈에 이미 참여했습니다.");

        verify(quizMapper, never()).insertAttempt(anyLong(), any(), anyLong(), anyLong(), anyBoolean());
        verify(coinService, never()).earn(anyLong(), anyInt());
    }

    @Test
    void getQuizHistory_countsConsecutiveCorrectDaysFromYesterday() {
        LocalDate today = LocalDate.now();
        when(quizMapper.findAttemptHistoryByUserId(USER_ID)).thenReturn(List.of(
                new QuizAttemptHistoryResponse(today.minusDays(1), 2L, "어제 문제", true, 50),
                new QuizAttemptHistoryResponse(today.minusDays(2), 3L, "그제 문제", true, 50),
                new QuizAttemptHistoryResponse(today.minusDays(3), 4L, "3일 전 문제", false, 0)
        ));

        var response = quizService.getQuizHistory(USER_ID);

        assertThat(response.getCurrentStreak()).isEqualTo(2);
        assertThat(response.getAttempts()).hasSize(3);
    }
}

package com.roommade.domain.quiz.service;

import com.roommade.domain.coin.service.CoinService;
import com.roommade.domain.quiz.code.QuizErrorCode;
import com.roommade.domain.quiz.dto.response.QuizAnswerEvaluationResponse;
import com.roommade.domain.quiz.dto.response.QuizAnswerSubmitResponse;
import com.roommade.domain.quiz.dto.response.QuizAttemptHistoryResponse;
import com.roommade.domain.quiz.dto.response.QuizHistoryResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;
import com.roommade.domain.quiz.mapper.QuizMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private static final int QUIZ_REWARD_POINT = 50;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final QuizMapper quizMapper;
    private final CoinService coinService;

    @Override
    @Transactional
    public TodayQuizResponse getTodayQuiz(Long userId) {
        LocalDate quizDate = today();
        quizMapper.insertDailyQuizIfAbsent(quizDate);

        TodayQuizResponse todayQuiz = quizMapper.findTodayQuiz(quizDate, userId);
        if (todayQuiz == null) {
            throw new BusinessException(QuizErrorCode.ACTIVE_QUIZ_NOT_FOUND);
        }
        return todayQuiz;
    }

    @Override
    @Transactional
    public QuizAnswerSubmitResponse submitTodayQuizAnswer(Long userId, Long selectedChoiceId) {
        LocalDate quizDate = today();
        quizMapper.insertDailyQuizIfAbsent(quizDate);

        TodayQuizResponse todayQuiz = quizMapper.findTodayQuiz(quizDate, userId);
        if (todayQuiz == null) {
            throw new BusinessException(QuizErrorCode.ACTIVE_QUIZ_NOT_FOUND);
        }
        if (quizMapper.existsAttemptByUserIdAndQuizDate(userId, quizDate)) {
            throw new BusinessException(QuizErrorCode.QUIZ_ALREADY_ATTEMPTED);
        }

        QuizAnswerEvaluationResponse evaluation =
                quizMapper.findAnswerEvaluation(todayQuiz.getQuizQuestionId(), selectedChoiceId);
        if (evaluation == null) {
            throw new BusinessException(QuizErrorCode.INVALID_QUIZ_CHOICE);
        }

        try {
            quizMapper.insertAttempt(
                    userId,
                    quizDate,
                    todayQuiz.getQuizQuestionId(),
                    selectedChoiceId,
                    evaluation.isCorrect());
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(QuizErrorCode.QUIZ_ALREADY_ATTEMPTED);
        }

        int earnedPoint = evaluation.isCorrect() ? QUIZ_REWARD_POINT : 0;
        int coinBalance = earnedPoint > 0
                ? coinService.earn(userId, earnedPoint)
                : coinService.getBalance(userId).getBalance();

        List<QuizAttemptHistoryResponse> attempts = quizMapper.findAttemptHistoryByUserId(userId);
        return new QuizAnswerSubmitResponse(
                quizDate,
                evaluation.isCorrect(),
                evaluation.getCorrectChoiceContent(),
                evaluation.getExplanation(),
                earnedPoint,
                calculateCurrentStreak(attempts, quizDate),
                coinBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizHistoryResponse getQuizHistory(Long userId) {
        List<QuizAttemptHistoryResponse> attempts = quizMapper.findAttemptHistoryByUserId(userId);
        return new QuizHistoryResponse(calculateCurrentStreak(attempts, today()), attempts);
    }

    private int calculateCurrentStreak(List<QuizAttemptHistoryResponse> attempts, LocalDate today) {
        if (attempts.isEmpty()) {
            return 0;
        }

        LocalDate latestAttemptDate = attempts.get(0).getQuizDate();
        if (latestAttemptDate.isBefore(today.minusDays(1))) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = latestAttemptDate;
        for (QuizAttemptHistoryResponse attempt : attempts) {
            if (!attempt.getQuizDate().equals(expectedDate) || !attempt.isCorrect()) {
                break;
            }
            streak++;
            expectedDate = expectedDate.minusDays(1);
        }
        return streak;
    }

    private LocalDate today() {
        return LocalDate.now(KOREA_ZONE_ID);
    }
}

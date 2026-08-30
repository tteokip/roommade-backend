package com.roommade.domain.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.roommade.domain.quiz.dto.response.QuizAnswerEvaluationResponse;
import com.roommade.domain.quiz.dto.response.TodayQuizResponse;
import com.roommade.domain.quiz.mapper.QuizMapper;
import com.roommade.global.exception.BusinessException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로컬 Docker Compose MySQL을 사용하는 퀴즈 통합 테스트다.
 * 각 테스트는 @Transactional에 의해 종료 시 자동 롤백된다.
 */
@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestPropertySource(properties = {
        "FSS_API_KEY=test-key",
        "YOUTH_POLICY_API_KEY=test-key"
})
@Transactional
class QuizServiceIntegrationTest {

    private static final long USER_ID = 980_001L;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizMapper quizMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                USER_ID, "quiz-integration-test@example.com", "encoded-password");
    }

    @Test
    @DisplayName("오늘의 퀴즈를 제출하면 정오답을 저장하고 정답 시 50코인을 적립한다")
    void submitsTodayQuizAndRewardsCorrectAnswer() {
        TodayQuizResponse todayQuiz = quizService.getTodayQuiz(USER_ID);
        Long selectedChoiceId = todayQuiz.getChoices().get(0).getChoiceId();
        QuizAnswerEvaluationResponse evaluation =
                quizMapper.findAnswerEvaluation(todayQuiz.getQuizQuestionId(), selectedChoiceId);

        var result = quizService.submitTodayQuizAnswer(USER_ID, selectedChoiceId);

        assertThat(result.isCorrect()).isEqualTo(evaluation.isCorrect());
        assertThat(result.getEarnedPoint()).isEqualTo(evaluation.isCorrect() ? 50 : 0);
        assertThat(result.getCurrentStreak()).isEqualTo(evaluation.isCorrect() ? 1 : 0);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_quiz_attempts WHERE user_id = ?", Integer.class, USER_ID)).isEqualTo(1);

        Integer balance = jdbcTemplate.query(
                "SELECT balance FROM coin_wallets WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("balance"), USER_ID)
                .stream()
                .findFirst()
                .orElse(0);
        assertThat(balance).isEqualTo(evaluation.isCorrect() ? 50 : 0);
    }

    @Test
    @DisplayName("같은 사용자는 같은 날 퀴즈를 두 번 제출할 수 없다")
    void preventsSecondAttemptOnSameDay() {
        TodayQuizResponse todayQuiz = quizService.getTodayQuiz(USER_ID);
        Long selectedChoiceId = todayQuiz.getChoices().get(0).getChoiceId();
        quizService.submitTodayQuizAnswer(USER_ID, selectedChoiceId);

        assertThatThrownBy(() -> quizService.submitTodayQuizAnswer(USER_ID, selectedChoiceId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("오늘의 퀴즈에 이미 참여했습니다.");
    }
}

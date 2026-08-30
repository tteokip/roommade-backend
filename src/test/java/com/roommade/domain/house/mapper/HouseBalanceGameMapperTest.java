package com.roommade.domain.house.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.house.dto.response.PreferenceAnswerResponse;
import com.roommade.domain.house.dto.response.PreferenceQuestionResponse;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

/**
 * root-context.xml을 그대로 로드하는 통합 테스트다. 로컬 Docker Compose MySQL이 떠 있어야 하고
 * (docker compose up -d), Flyway가 V3 마이그레이션으로 시드한 preference_questions를 그대로 사용한다.
 */
@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Transactional
class HouseBalanceGameMapperTest {

    @Autowired
    private HouseBalanceGameMapper houseBalanceGameMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("V3로 시드된 10개 질문을 question_order 오름차순으로 조회한다")
    void findsSeededQuestionsOrderedByQuestionOrder() {
        List<PreferenceQuestionResponse> questions = houseBalanceGameMapper.findQuestionsOrderByQuestionOrder();

        assertThat(questions).hasSize(10);
        assertThat(questions)
                .extracting(question -> question.getOptionAFactor() + "-" + question.getOptionBFactor())
                .containsExactly(
                        "MONTHLY_COST-COMMUTE",
                        "STATION-AREA",
                        "OPTION-MONTHLY_COST",
                        "MONTHLY_COST-DEPOSIT",
                        "AREA-COMMUTE",
                        "MONTHLY_COST-STATION",
                        "DEPOSIT-AREA",
                        "OPTION-STATION",
                        "DEPOSIT-COMMUTE",
                        "DEPOSIT-OPTION");
    }

    @Test
    @DisplayName("같은 (comparison_id, question_id)에 다시 답하면 기존 답변을 덮어쓴다")
    void upsertAnswerUpdatesExistingAnswer() {
        insertUser(920_101L);
        Long comparisonId = insertComparison(930_101L, 920_101L);
        Long questionId = houseBalanceGameMapper.findQuestionsOrderByQuestionOrder().get(0).getId();

        houseBalanceGameMapper.insertOrUpdateAnswer(comparisonId, questionId, "A");
        houseBalanceGameMapper.insertOrUpdateAnswer(comparisonId, questionId, "B");

        List<PreferenceAnswerResponse> answers = houseBalanceGameMapper.findAnswersByComparisonId(comparisonId);
        assertThat(answers).hasSize(1);
        assertThat(answers.get(0).getQuestionId()).isEqualTo(questionId);
        assertThat(answers.get(0).getSelectedSide()).isEqualTo("B");
    }

    @Test
    @DisplayName("다른 비교의 답변은 조회되지 않는다")
    void doesNotLeakOtherComparisonsAnswers() {
        insertUser(920_102L);
        insertUser(920_103L);
        Long comparisonId1 = insertComparison(930_102L, 920_102L);
        Long comparisonId2 = insertComparison(930_103L, 920_103L);
        List<PreferenceQuestionResponse> questions = houseBalanceGameMapper.findQuestionsOrderByQuestionOrder();
        Long questionId1 = questions.get(0).getId();
        Long questionId2 = questions.get(1).getId();

        houseBalanceGameMapper.insertOrUpdateAnswer(comparisonId1, questionId1, "A");
        houseBalanceGameMapper.insertOrUpdateAnswer(comparisonId2, questionId2, "B");

        List<PreferenceAnswerResponse> answersForComparison1 =
                houseBalanceGameMapper.findAnswersByComparisonId(comparisonId1);
        assertThat(answersForComparison1).hasSize(1);
        assertThat(answersForComparison1.get(0).getQuestionId()).isEqualTo(questionId1);
        assertThat(answersForComparison1.get(0).getSelectedSide()).isEqualTo("A");
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "test-user-" + id + "@example.com", "encoded-password");
    }

    private Long insertComparison(long id, long userId) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO house_comparisons (id, user_id, status, created_at, updated_at) "
                        + "VALUES (?, ?, 'DRAFT', ?, ?)",
                id, userId, now, now);
        return id;
    }
}

package com.roommade.domain.living.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.living.dto.response.ChallengeRewardResponse;
import com.roommade.domain.living.dto.response.LatestChallengeResultResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
 * root-context.xml을 그대로 로드하는 통합 테스트다. 로컬 Docker Compose MySQL이 떠 있어야 한다
 * (docker compose up -d).
 */
@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Transactional
class ChallengeMapperTest {

    @Autowired
    private ChallengeMapper challengeMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("마감 안 된 지난 날짜의 지출을 달성 레벨과 함께 마감한다")
    void closesUnclosedPastDayWithAchievedLevel() {
        insertUser(940_101L);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long dailyLivingCostId = insertDailyLivingCost(940_101L, yesterday, 12_000L);
        LocalDateTime closedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        int closedCount = challengeMapper.closeDueChallenges(LocalDate.now(), closedAt);

        assertThat(closedCount).isEqualTo(1);
        Long achievedLevelId = jdbcTemplate.queryForObject(
                "SELECT achieved_level_id FROM daily_challenges WHERE daily_living_cost_id = ?",
                Long.class, dailyLivingCostId);
        assertThat(achievedLevelId).isEqualTo(levelId(2));

        List<ChallengeRewardResponse> rewards = challengeMapper.findRewardsClosedAt(closedAt);
        assertThat(rewards).hasSize(1);
        assertThat(rewards.get(0).getUserId()).isEqualTo(940_101L);
        assertThat(rewards.get(0).getRewardCoin()).isEqualTo(35);
    }

    @Test
    @DisplayName("모든 레벨 기준을 초과하는 지출은 달성 레벨 없이 마감되고 보상 대상에서 빠진다")
    void closesWithNullAchievedLevelWhenSpendingExceedsAllLevels() {
        insertUser(940_102L);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long dailyLivingCostId = insertDailyLivingCost(940_102L, yesterday, 999_999L);
        LocalDateTime closedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        int closedCount = challengeMapper.closeDueChallenges(LocalDate.now(), closedAt);

        assertThat(closedCount).isEqualTo(1);
        Long achievedLevelId = jdbcTemplate.queryForObject(
                "SELECT achieved_level_id FROM daily_challenges WHERE daily_living_cost_id = ?",
                Long.class, dailyLivingCostId);
        assertThat(achievedLevelId).isNull();
        assertThat(challengeMapper.findRewardsClosedAt(closedAt)).isEmpty();
    }

    @Test
    @DisplayName("오늘 날짜의 지출은 마감하지 않는다")
    void doesNotCloseTodaysCost() {
        insertUser(940_103L);
        insertDailyLivingCost(940_103L, LocalDate.now(), 5_000L);

        int closedCount = challengeMapper.closeDueChallenges(LocalDate.now(), LocalDateTime.now());

        assertThat(closedCount).isZero();
    }

    @Test
    @DisplayName("이미 마감된 날짜는 다시 마감하지 않는다")
    void doesNotCloseAlreadyClosedDay() {
        insertUser(940_104L);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long dailyLivingCostId = insertDailyLivingCost(940_104L, yesterday, 5_000L);
        jdbcTemplate.update(
                "INSERT INTO daily_challenges (daily_living_cost_id, achieved_level_id, closed_at) "
                        + "VALUES (?, ?, ?)",
                dailyLivingCostId, levelId(3), LocalDateTime.now());

        int closedCount = challengeMapper.closeDueChallenges(LocalDate.now(), LocalDateTime.now());

        assertThat(closedCount).isZero();
    }

    @Test
    @DisplayName("가장 최근 마감된 챌린지 결과를 반환한다")
    void returnsMostRecentClosedResult() {
        insertUser(940_105L);
        insertClosedDailyChallenge(940_105L, LocalDate.now().minusDays(2), 2);
        insertClosedDailyChallenge(940_105L, LocalDate.now().minusDays(1), 3);

        LatestChallengeResultResponse result = challengeMapper.findLatestResult(940_105L);

        assertThat(result.getChallengeDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(result.getAchievedLevel()).isEqualTo(3);
        assertThat(result.getRewardCoin()).isEqualTo(50);
    }

    @Test
    @DisplayName("달성 레벨이 없는 날은 achievedLevel/rewardCoin이 null로 반환된다")
    void returnsNullLevelWhenNotAchieved() {
        insertUser(940_106L);
        insertClosedDailyChallenge(940_106L, LocalDate.now().minusDays(1), null);

        LatestChallengeResultResponse result = challengeMapper.findLatestResult(940_106L);

        assertThat(result.getAchievedLevel()).isNull();
        assertThat(result.getRewardCoin()).isNull();
    }

    @Test
    @DisplayName("마감 기록이 없으면 null을 반환한다")
    void returnsNullWhenNoClosedRecordExists() {
        insertUser(940_107L);

        assertThat(challengeMapper.findLatestResult(940_107L)).isNull();
    }

    private void insertClosedDailyChallenge(long userId, LocalDate spendingDate, Integer level) {
        Long dailyLivingCostId = insertDailyLivingCost(userId, spendingDate, 5_000L);
        Long achievedLevelId = level == null ? null : levelId(level);
        jdbcTemplate.update(
                "INSERT INTO daily_challenges (daily_living_cost_id, achieved_level_id, closed_at) "
                        + "VALUES (?, ?, ?)",
                dailyLivingCostId, achievedLevelId, LocalDateTime.now());
    }

    private Long levelId(int level) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM challenge_levels WHERE level = ?", Long.class, level);
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "challenge-test-user-" + id + "@example.com", "encoded-password");
    }

    private Long insertDailyLivingCost(long userId, LocalDate spendingDate, long totalAmount) {
        jdbcTemplate.update(
                "INSERT INTO daily_living_costs (user_id, spending_date, total_amount) "
                        + "VALUES (?, ?, ?)",
                userId, spendingDate, totalAmount);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM daily_living_costs WHERE user_id = ? AND spending_date = ?",
                Long.class, userId, spendingDate);
    }
}

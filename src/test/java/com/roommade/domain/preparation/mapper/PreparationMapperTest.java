package com.roommade.domain.preparation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class PreparationMapperTest {

    @Autowired
    private PreparationMapper preparationMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("사용자 프로필의 월 소득과 월세 상한을 조회한다")
    void findsRirProfileByUserId() {
        insertUser(940_001L);
        insertUserProfile(950_001L, 940_001L, 1_870_000L, 0L, 650_000L);

        RirProfileResponse result = preparationMapper.findRirProfileByUserId(940_001L);

        assertThat(result.getMonthlyIncome()).isEqualTo(1_870_000L);
        assertThat(result.getMonthlyRentLimit()).isEqualTo(650_000L);
    }

    @Test
    @DisplayName("사용자 프로필이 없으면 null을 반환한다")
    void returnsNullWhenRirProfileDoesNotExist() {
        insertUser(940_002L);

        RirProfileResponse result = preparationMapper.findRirProfileByUserId(940_002L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("사용자 목표 보증금과 현재 마련 금액을 원 단위로 조회한다")
    void findsDepositProgressByUserId() {
        insertUser(940_003L);
        insertUserProfile(950_003L, 940_003L, 1_870_000L, 50_000_000L, 650_000L);
        insertIndependenceProgress(960_003L, 940_003L, 35_123_456L);

        DepositProgressSourceResponse result =
                preparationMapper.findDepositProgressByUserId(940_003L);

        assertThat(result.getTargetDeposit()).isEqualTo(50_000_000L);
        assertThat(result.getCurrentDeposit()).isEqualTo(35_123_456L);
    }

    @Test
    @DisplayName("자립 준비 진행 데이터가 없으면 보증금 조회 결과는 null이다")
    void returnsNullWhenIndependenceProgressDoesNotExist() {
        insertUser(940_004L);
        insertUserProfile(950_004L, 940_004L, 1_870_000L, 50_000_000L, 650_000L);

        DepositProgressSourceResponse result =
                preparationMapper.findDepositProgressByUserId(940_004L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("최초 비교 매물 등록 완료 시간을 한 번만 기록한다")
    void marksHouseComparisonCompletedOnlyOnce() {
        insertUser(940_005L);
        insertIndependenceProgress(960_005L, 940_005L, 0L);

        int firstUpdatedRows = preparationMapper.markHouseComparisonCompleted(940_005L);
        LocalDateTime firstCompletedAt =
                preparationMapper.findHouseComparisonCompletedAtByUserId(940_005L);
        int secondUpdatedRows = preparationMapper.markHouseComparisonCompleted(940_005L);
        LocalDateTime secondCompletedAt =
                preparationMapper.findHouseComparisonCompletedAtByUserId(940_005L);

        assertThat(firstUpdatedRows).isEqualTo(1);
        assertThat(firstCompletedAt).isNotNull();
        assertThat(secondUpdatedRows).isZero();
        assertThat(secondCompletedAt).isEqualTo(firstCompletedAt);
    }

    @Test
    @DisplayName("사용자 입주 예정일과 독립 이후 전환 시간을 조회한다")
    void findsMoveInStateByUserId() {
        insertUser(940_006L);
        insertIndependenceProgress(960_006L, 940_006L, 0L);
        LocalDate moveInDate = LocalDate.of(2026, 8, 30);
        LocalDateTime movedInAt = LocalDateTime.of(2026, 8, 30, 18, 0);
        jdbcTemplate.update(
                "UPDATE independence_progress SET move_in_date = ?, moved_in_at = ? "
                        + "WHERE user_id = ?",
                moveInDate, movedInAt, 940_006L);

        MoveInStateSourceResponse result =
                preparationMapper.findMoveInStateByUserId(940_006L);

        assertThat(result.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(result.getMovedInAt()).isEqualTo(movedInAt);
    }

    @Test
    @DisplayName("등록 매물 입주 확정 시 매물 ID와 입주 예정일을 한 번만 기록한다")
    void updatesComparisonHouseMoveInOnlyOnce() {
        insertUser(9_940_007_001L);
        insertIndependenceProgress(9_960_007_001L, 9_940_007_001L, 0L);
        insertComparison(9_970_007_001L, 9_940_007_001L);
        insertHouse(9_980_007_001L, 9_970_007_001L, "A");
        LocalDate moveInDate = LocalDate.of(2099, 9, 15);

        int firstUpdatedRows =
                preparationMapper.updateMoveInSchedule(
                        9_940_007_001L, 9_980_007_001L, moveInDate, null);
        int secondUpdatedRows =
                preparationMapper.updateMoveInSchedule(
                        9_940_007_001L, 9_980_007_001L, moveInDate, null);

        assertThat(firstUpdatedRows).isEqualTo(1);
        assertThat(secondUpdatedRows).isZero();
        MoveInStateSourceResponse state =
                preparationMapper.findMoveInStateByUserId(9_940_007_001L);
        assertThat(state.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(state.getMovedInAt()).isNull();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT confirmed_house_id FROM independence_progress WHERE user_id = ?",
                Long.class,
                9_940_007_001L)).isEqualTo(9_980_007_001L);
    }

    @Test
    @DisplayName("다른 집 입주 확정 시 매물 ID 없이 입주 정보를 기록한다")
    void updatesOtherHouseMoveInWithoutHouseId() {
        insertUser(9_940_008_001L);
        insertIndependenceProgress(9_960_008_001L, 9_940_008_001L, 0L);
        LocalDate moveInDate = LocalDate.of(2026, 9, 15);
        LocalDateTime movedInAt = LocalDateTime.of(2026, 9, 15, 0, 5);

        int updatedRows = preparationMapper.updateMoveInSchedule(
                9_940_008_001L, null, moveInDate, movedInAt);

        assertThat(updatedRows).isEqualTo(1);
        MoveInStateSourceResponse state =
                preparationMapper.findMoveInStateByUserId(9_940_008_001L);
        assertThat(state.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(state.getMovedInAt()).isEqualTo(movedInAt);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT confirmed_house_id IS NULL FROM independence_progress WHERE user_id = ?",
                Boolean.class,
                9_940_008_001L)).isTrue();
    }

    @Test
    @DisplayName("입주일이 도래한 미전환 사용자만 독립 이후로 전환한다")
    void updatesOnlyDueMoveIns() {
        insertUser(9_940_010_001L);
        insertUser(9_940_010_002L);
        insertUser(9_940_010_003L);
        insertIndependenceProgress(9_960_010_001L, 9_940_010_001L, 0L);
        insertIndependenceProgress(9_960_010_002L, 9_940_010_002L, 0L);
        insertIndependenceProgress(9_960_010_003L, 9_940_010_003L, 0L);
        jdbcTemplate.update(
                "UPDATE independence_progress SET move_in_date = ? WHERE user_id = ?",
                LocalDate.of(2026, 9, 1), 9_940_010_001L);
        jdbcTemplate.update(
                "UPDATE independence_progress SET move_in_date = ? WHERE user_id = ?",
                LocalDate.of(2026, 9, 3), 9_940_010_002L);
        jdbcTemplate.update(
                "UPDATE independence_progress SET move_in_date = ?, moved_in_at = ? "
                        + "WHERE user_id = ?",
                LocalDate.of(2026, 9, 1),
                LocalDateTime.of(2026, 9, 1, 0, 5),
                9_940_010_003L);
        LocalDateTime transitionedAt = LocalDateTime.of(2026, 9, 2, 0, 5);

        int updatedRows = preparationMapper.updateDueMoveIns(
                LocalDate.of(2026, 9, 2), transitionedAt);

        assertThat(updatedRows).isEqualTo(1);
        assertThat(preparationMapper.findMoveInStateByUserId(9_940_010_001L)
                .getMovedInAt()).isEqualTo(transitionedAt);
        assertThat(preparationMapper.findMoveInStateByUserId(9_940_010_002L)
                .getMovedInAt()).isNull();
        assertThat(preparationMapper.findMoveInStateByUserId(9_940_010_003L)
                .getMovedInAt()).isEqualTo(LocalDateTime.of(2026, 9, 1, 0, 5));
    }

    @Test
    @DisplayName("자립 준비 진행 데이터 존재 여부를 조회한다")
    void checksIndependenceProgressExistence() {
        insertUser(9_940_009_001L);
        insertIndependenceProgress(9_960_009_001L, 9_940_009_001L, 0L);

        assertThat(preparationMapper.existsIndependenceProgressByUserId(9_940_009_001L)).isTrue();
        assertThat(preparationMapper.existsIndependenceProgressByUserId(999_999L)).isFalse();
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "preparation-test-user-" + id + "@example.com", "encoded-password");
    }

    private void insertUserProfile(
            long id,
            long userId,
            long monthlyIncome,
            long depositLimit,
            long monthlyRentLimit) {
        jdbcTemplate.update(
                "INSERT INTO user_profiles "
                        + "(id, user_id, name, birth_date, monthly_income, deposit_limit, monthly_rent_limit) "
                        + "VALUES (?, ?, '테스트 사용자', '2000-01-01', ?, ?, ?)",
                id, userId, monthlyIncome, depositLimit, monthlyRentLimit);
    }

    private void insertIndependenceProgress(long id, long userId, long currentDeposit) {
        jdbcTemplate.update(
                "INSERT INTO independence_progress (id, user_id, current_deposit) "
                        + "VALUES (?, ?, ?)",
                id, userId, currentDeposit);
    }

    private void insertComparison(long id, long userId) {
        jdbcTemplate.update(
                "INSERT INTO house_comparisons (id, user_id) VALUES (?, ?)",
                id, userId);
    }

    private void insertHouse(long id, long comparisonId, String houseType) {
        jdbcTemplate.update(
                "INSERT INTO houses (id, comparison_id, house_type) VALUES (?, ?, ?)",
                id, comparisonId, houseType);
    }

}

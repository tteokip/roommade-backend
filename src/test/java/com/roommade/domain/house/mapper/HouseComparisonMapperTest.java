package com.roommade.domain.house.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
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
class HouseComparisonMapperTest {

    @Autowired
    private HouseComparisonMapper houseComparisonMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("비교가 없으면 null을 반환한다")
    void returnsNullWhenNoComparisonExists() {
        insertUser(910_001L);

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_001L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("비교는 있지만 등록된 매물이 없으면 A와 B를 모두 null로 반환한다")
    void returnsComparisonWithoutHouses() {
        insertUser(910_002L);
        insertComparison(920_002L, 910_002L, LocalDateTime.now());

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_002L);

        assertThat(result.getComparisonId()).isEqualTo(920_002L);
        assertThat(result.getHouseA()).isNull();
        assertThat(result.getHouseB()).isNull();
        assertThat(result.isBalanceGameAvailable()).isFalse();
    }

    @Test
    @DisplayName("A 매물만 등록되어 있으면 A만 반환한다")
    void returnsOnlyHouseAWhenOnlyAIsRegistered() {
        insertUser(910_003L);
        insertComparison(920_003L, 910_003L, LocalDateTime.now());
        insertHouse(930_003L, 920_003L, "A", "서울시 강남구");

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_003L);

        assertThat(result.getHouseA().getId()).isEqualTo(930_003L);
        assertThat(result.getHouseA().getLocation()).isEqualTo("서울시 강남구");
        assertThat(result.getHouseB()).isNull();
        assertThat(result.isBalanceGameAvailable()).isFalse();
    }

    @Test
    @DisplayName("B 매물만 등록되어 있으면 B만 반환한다")
    void returnsOnlyHouseBWhenOnlyBIsRegistered() {
        insertUser(910_004L);
        insertComparison(920_004L, 910_004L, LocalDateTime.now());
        insertHouse(930_004L, 920_004L, "B", "서울시 마포구");

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_004L);

        assertThat(result.getHouseA()).isNull();
        assertThat(result.getHouseB().getId()).isEqualTo(930_004L);
        assertThat(result.isBalanceGameAvailable()).isFalse();
    }

    @Test
    @DisplayName("A와 B가 모두 등록되어 있으면 밸런스게임을 활성화한다")
    void returnsBalanceGameAvailableWhenBothRegistered() {
        insertUser(910_005L);
        insertComparison(920_005L, 910_005L, LocalDateTime.now());
        insertHouse(930_005L, 920_005L, "A", "서울시 강남구");
        insertHouse(930_006L, 920_005L, "B", "서울시 마포구");

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_005L);

        assertThat(result.getHouseA().getId()).isEqualTo(930_005L);
        assertThat(result.getHouseB().getId()).isEqualTo(930_006L);
        assertThat(result.isBalanceGameAvailable()).isTrue();
    }

    @Test
    @DisplayName("다른 사용자의 비교와 매물은 반환하지 않는다")
    void doesNotLeakOtherUsersData() {
        insertUser(910_006L);
        insertUser(910_007L);
        insertComparison(920_006L, 910_006L, LocalDateTime.now());
        insertComparison(920_007L, 910_007L, LocalDateTime.now());
        insertHouse(930_007L, 920_006L, "A", "본인 매물");
        insertHouse(930_008L, 920_007L, "A", "타인 매물");

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_006L);

        assertThat(result.getComparisonId()).isEqualTo(920_006L);
        assertThat(result.getHouseA().getLocation()).isEqualTo("본인 매물");
    }

    @Test
    @DisplayName("매물 등록 순서와 관계없이 유형에 따라 A와 B를 매핑한다")
    void mapsByHouseTypeRegardlessOfInsertOrder() {
        insertUser(910_009L);
        insertComparison(920_010L, 910_009L, LocalDateTime.now());
        insertHouse(930_010L, 920_010L, "B", "매물 B");
        insertHouse(930_009L, 920_010L, "A", "매물 A");

        HouseComparisonCurrentResponse result = houseComparisonMapper.findCurrentByUserId(910_009L);

        assertThat(result.getHouseA().getLocation()).isEqualTo("매물 A");
        assertThat(result.getHouseB().getLocation()).isEqualTo("매물 B");
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "test-user-" + id + "@example.com", "encoded-password");
    }

    private void insertComparison(long id, long userId, LocalDateTime createdAt) {
        jdbcTemplate.update(
                "INSERT INTO house_comparisons (id, user_id, status, created_at, updated_at) "
                        + "VALUES (?, ?, 'DRAFT', ?, ?)",
                id, userId, createdAt, createdAt);
    }

    private void insertHouse(long id, long comparisonId, String houseType, String location) {
        jdbcTemplate.update(
                "INSERT INTO houses (id, comparison_id, house_type, location) VALUES (?, ?, ?, ?)",
                id, comparisonId, houseType, location);
    }
}

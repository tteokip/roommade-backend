package com.roommade.domain.preparation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.preparation.dto.response.RirProfileResponse;
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
        insertUserProfile(950_001L, 940_001L, 187L, 65L);

        RirProfileResponse result = preparationMapper.findRirProfileByUserId(940_001L);

        assertThat(result.getMonthlyIncome()).isEqualTo(187L);
        assertThat(result.getMonthlyRentLimit()).isEqualTo(65L);
    }

    @Test
    @DisplayName("사용자 프로필이 없으면 null을 반환한다")
    void returnsNullWhenRirProfileDoesNotExist() {
        insertUser(940_002L);

        RirProfileResponse result = preparationMapper.findRirProfileByUserId(940_002L);

        assertThat(result).isNull();
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "preparation-test-user-" + id + "@example.com", "encoded-password");
    }

    private void insertUserProfile(
            long id, long userId, long monthlyIncome, long monthlyRentLimit) {
        jdbcTemplate.update(
                "INSERT INTO user_profiles "
                        + "(id, user_id, name, birth_date, monthly_income, deposit_limit, monthly_rent_limit) "
                        + "VALUES (?, ?, '테스트 사용자', '2000-01-01', ?, 0, ?)",
                id, userId, monthlyIncome, monthlyRentLimit);
    }
}

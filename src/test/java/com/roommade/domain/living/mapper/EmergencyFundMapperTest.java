package com.roommade.domain.living.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

/**
 * root-context.xml을 그대로 로드하는 통합 테스트다. 로컬 Docker Compose MySQL이 떠 있어야 한다
 * (docker compose up -d).
 */
@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Transactional
class EmergencyFundMapperTest {

    @Autowired
    private EmergencyFundMapper emergencyFundMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("행이 없으면 null을 반환한다")
    void returnsNullWhenNoRowExists() {
        insertUser(940_001L);

        EmergencyFundResponse result = emergencyFundMapper.findByUserId(940_001L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("insert로 생성한 행을 achieved=false로 조회한다")
    void findsInsertedRowAsNotAchieved() {
        insertUser(940_002L);

        emergencyFundMapper.insert(940_002L, 500_000L, null);
        EmergencyFundResponse result = emergencyFundMapper.findByUserId(940_002L);

        assertThat(result.getTargetAmount()).isEqualTo(500_000L);
        assertThat(result.getCurrentAmount()).isZero();
        assertThat(result.isAchieved()).isFalse();
        assertThat(result.getAchievedAt()).isNull();
    }

    @Test
    @DisplayName("achieved_at이 있는 행은 achieved=true로 조회된다")
    void findsRowWithAchievedAtAsAchieved() {
        insertUser(940_003L);
        LocalDateTime achievedAt = LocalDateTime.now().withNano(0);

        emergencyFundMapper.insert(940_003L, 500_000L, achievedAt);
        EmergencyFundResponse result = emergencyFundMapper.findByUserId(940_003L);

        assertThat(result.isAchieved()).isTrue();
        assertThat(result.getAchievedAt()).isEqualTo(achievedAt);
    }

    @Test
    @DisplayName("updateTarget으로 target_amount와 achieved_at을 갱신한다")
    void updatesTargetAndAchievedAt() {
        insertUser(940_004L);
        emergencyFundMapper.insert(940_004L, 300_000L, null);
        LocalDateTime achievedAt = LocalDateTime.now().withNano(0);

        emergencyFundMapper.updateTarget(940_004L, 800_000L, achievedAt);
        EmergencyFundResponse result = emergencyFundMapper.findByUserId(940_004L);

        assertThat(result.getTargetAmount()).isEqualTo(800_000L);
        assertThat(result.isAchieved()).isTrue();
        assertThat(result.getAchievedAt()).isEqualTo(achievedAt);
    }

    @Test
    @DisplayName("같은 사용자에 두 번 insert하면 (user_id) UNIQUE 위반이 난다")
    void insertTwiceForSameUserViolatesUniqueConstraint() {
        insertUser(940_005L);
        emergencyFundMapper.insert(940_005L, 300_000L, null);

        assertThatThrownBy(() -> emergencyFundMapper.insert(940_005L, 500_000L, null))
                .isInstanceOf(DuplicateKeyException.class);
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "test-user-" + id + "@example.com", "encoded-password");
    }
}
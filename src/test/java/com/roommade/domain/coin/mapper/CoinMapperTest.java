package com.roommade.domain.coin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestPropertySource(properties = {
        "FSS_API_KEY=test-key",
        "YOUTH_POLICY_API_KEY=test-key"
})
@Transactional
class CoinMapperTest {

    private static final long USER_ID = 9_910_001_001L;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash) VALUES (?, ?, ?)",
                USER_ID,
                "coin-mapper-test@example.com",
                "encoded-password");
    }

    @Test
    void managesWalletBalanceWithoutNegativeSpending() {
        assertThat(coinMapper.findBalanceByUserId(USER_ID)).isZero();

        assertThat(coinMapper.insertWalletIfAbsent(USER_ID)).isEqualTo(1);
        assertThat(coinMapper.insertWalletIfAbsent(USER_ID)).isZero();
        assertThat(coinMapper.updateBalanceForEarning(USER_ID, 100)).isEqualTo(1);
        assertThat(coinMapper.findBalanceByUserId(USER_ID)).isEqualTo(100);

        assertThat(coinMapper.updateBalanceForSpending(USER_ID, 40)).isEqualTo(1);
        assertThat(coinMapper.findBalanceByUserId(USER_ID)).isEqualTo(60);
        assertThat(coinMapper.updateBalanceForSpending(USER_ID, 61)).isZero();
        assertThat(coinMapper.findBalanceByUserId(USER_ID)).isEqualTo(60);
    }

    @Test
    void returnsNullForUnknownUser() {
        assertThat(coinMapper.findBalanceByUserId(9_999_999_999L)).isNull();
    }
}

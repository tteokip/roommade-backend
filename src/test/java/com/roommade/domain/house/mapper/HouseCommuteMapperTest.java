package com.roommade.domain.house.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.roommade.domain.house.dto.response.WorkplaceAddressResponse;
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
class HouseCommuteMapperTest {

    @Autowired
    private HouseCommuteMapper houseCommuteMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("직장 도로명 주소를 조회한다")
    void findsWorkplaceAddress() {
        insertUser(940_001L);
        insertUserProfile(940_001L, "서울특별시 중구 을지로 65");

        WorkplaceAddressResponse result = houseCommuteMapper.findWorkplaceAddressByUserId(940_001L);

        assertThat(result.getWorkplaceRoadAddress()).isEqualTo("서울특별시 중구 을지로 65");
    }

    @Test
    @DisplayName("직장 도로명 주소가 비어 있으면 null을 반환한다")
    void returnsNullWhenWorkplaceAddressColumnsAreAllNull() {
        insertUser(940_002L);
        insertUserProfile(940_002L, null);

        WorkplaceAddressResponse result = houseCommuteMapper.findWorkplaceAddressByUserId(940_002L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("user_profiles 행 자체가 없으면 null을 반환한다")
    void returnsNullWhenUserProfileDoesNotExist() {
        insertUser(940_003L);

        WorkplaceAddressResponse result = houseCommuteMapper.findWorkplaceAddressByUserId(940_003L);

        assertThat(result).isNull();
    }

    private void insertUser(long id) {
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash, created_at, updated_at) "
                        + "VALUES (?, ?, ?, NOW(), NOW())",
                id, "test-user-" + id + "@example.com", "encoded-password");
    }

    private void insertUserProfile(long userId, String workplaceRoadAddress) {
        jdbcTemplate.update(
                "INSERT INTO user_profiles "
                        + "(user_id, name, birth_date, workplace_road_address, created_at, updated_at) "
                        + "VALUES (?, ?, '2000-01-01', ?, NOW(), NOW())",
                userId, "테스트 사용자", workplaceRoadAddress);
    }
}

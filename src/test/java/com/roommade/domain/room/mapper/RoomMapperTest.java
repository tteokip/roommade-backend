package com.roommade.domain.room.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.roommade.domain.room.dto.response.FurnitureOptionResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardSourceResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Transactional
class RoomMapperTest {

    private static final Long USER_ID = 9_910_001L;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
                "INSERT INTO users (id, email, password_hash) VALUES (?, ?, ?)",
                USER_ID, "room-mapper-test@example.com", "encoded-password");
    }

    @Test
    void grantsInitialFurnitureAndCreatesEachStageRewardOnce() {
        assertThat(roomMapper.insertInitialFurniture(USER_ID)).isEqualTo(1);
        assertThat(roomMapper.insertInitialFurniture(USER_ID)).isZero();

        List<RoomFurnitureResponse> owned = roomMapper.findOwnedFurnitureByUserId(USER_ID);
        assertThat(owned)
                .singleElement()
                .satisfies(furniture -> {
                    assertThat(furniture.getName()).isEqualTo("기본 창문");
                    assertThat(furniture.getAssetUrl())
                            .isEqualTo("/src/assets/room-layer/window-layer.png");
                    assertThat(furniture.isPlaced()).isTrue();
                });

        assertThat(roomMapper.insertRewardIfAbsent(USER_ID, 15)).isEqualTo(1);
        assertThat(roomMapper.insertRewardIfAbsent(USER_ID, 15)).isZero();

        FurnitureRewardSourceResponse reward =
                roomMapper.findPendingRewardsByUserId(USER_ID).get(0);
        List<FurnitureOptionResponse> choices =
                roomMapper.findSelectableFurniture(USER_ID, reward.getRewardStage());
        assertThat(choices).extracting(FurnitureOptionResponse::getName)
                .containsExactly("기본 침대", "기본 책상");
    }

    @Test
    void replacesPlacedFurnitureInTheSameCategory() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'",
                Long.class);
        Long basicBedId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = '기본 침대'",
                Long.class);
        jdbcTemplate.update(
                "INSERT INTO furniture "
                        + "(category_id, name, furniture_type, coin_price, asset_url, active) "
                        + "VALUES (?, '모던 침대', 'SHOP', 100, '/modern-bed.png', TRUE)",
                bedCategoryId);
        Long modernBedId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = '모던 침대'",
                Long.class);
        jdbcTemplate.update(
                "INSERT INTO user_furniture (user_id, furniture_id, is_placed) "
                        + "VALUES (?, ?, TRUE), (?, ?, FALSE)",
                USER_ID, basicBedId, USER_ID, modernBedId);

        assertThat(roomMapper.unplaceFurnitureInSameCategory(USER_ID, modernBedId))
                .isEqualTo(1);
        assertThat(roomMapper.updateFurniturePlacement(USER_ID, modernBedId, true))
                .isEqualTo(1);

        List<RoomFurnitureResponse> beds = roomMapper.findOwnedFurnitureByUserId(USER_ID);
        assertThat(beds).filteredOn(furniture -> furniture.getCategoryId().equals(bedCategoryId))
                .extracting(RoomFurnitureResponse::getName, RoomFurnitureResponse::isPlaced)
                .containsExactlyInAnyOrder(
                        tuple("기본 침대", false),
                        tuple("모던 침대", true));
    }
}

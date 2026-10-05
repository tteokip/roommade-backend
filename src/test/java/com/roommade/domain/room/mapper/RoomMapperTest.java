package com.roommade.domain.room.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.roommade.domain.room.dto.response.FurnitureOptionResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardSourceResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureResponse;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("카테고리로 상점 가구를 조회하고 보유 및 해금 여부를 반환한다")
    void findsShopFurnitureWithOwnedFlagFilteredByCategory() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'",
                Long.class);
        Long deskCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '책상'",
                Long.class);
        Long shopBedId = insertFurniture(bedCategoryId, "모던 침대", "SHOP", 300, true);
        insertFurniture(deskCategoryId, "모던 책상", "SHOP", 200, true);
        Long basicBedId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = '기본 침대'", Long.class);
        jdbcTemplate.update(
                "INSERT INTO user_furniture (user_id, furniture_id, is_placed) VALUES (?, ?, FALSE)",
                USER_ID, shopBedId);
        jdbcTemplate.update(
                "INSERT INTO user_furniture (user_id, furniture_id, is_placed) VALUES (?, ?, FALSE)",
                USER_ID, basicBedId);

        List<ShopFurnitureResponse> bedsOnly =
                roomMapper.findShopFurniture(USER_ID, bedCategoryId);
        assertThat(bedsOnly)
                .extracting(ShopFurnitureResponse::getName)
                .doesNotContain("모던 책상");
        assertThat(bedsOnly)
                .filteredOn(item -> item.getName().equals("모던 침대"))
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.getCoinPrice()).isEqualTo(300);
                    assertThat(item.isOwned()).isTrue();
                    assertThat(item.isUnlocked()).isTrue();
                });

        List<ShopFurnitureResponse> all = roomMapper.findShopFurniture(USER_ID, null);
        assertThat(all).extracting(ShopFurnitureResponse::getName)
                .contains("모던 침대", "모던 책상");
    }

    @Test
    @DisplayName("active 상태와 관계없이 상점 가구를 조회한다")
    void includesShopFurnitureRegardlessOfActiveStatus() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'",
                Long.class);
        insertFurniture(bedCategoryId, "단종 침대", "SHOP", 300, false);

        assertThat(roomMapper.findShopFurniture(USER_ID, bedCategoryId))
                .extracting(ShopFurnitureResponse::getName)
                .contains("단종 침대");
    }

    @Test
    @DisplayName("active 상태와 관계없이 상점 가구 가격을 조회한다")
    void findsPriceForShopFurnitureRegardlessOfActiveStatus() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'",
                Long.class);
        Long shopBedId = insertFurniture(bedCategoryId, "모던 침대", "SHOP", 300, true);
        Long inactiveShopBedId = insertFurniture(bedCategoryId, "단종 침대", "SHOP", 300, false);
        Long basicBedId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = '기본 침대'",
                Long.class);

        assertThat(roomMapper.findShopFurniturePrice(shopBedId)).isEqualTo(300);
        assertThat(roomMapper.findShopFurniturePrice(inactiveShopBedId)).isEqualTo(300);
        assertThat(roomMapper.findShopFurniturePrice(basicBedId)).isNull();
    }

    @Test
    @DisplayName("같은 카테고리의 기본 가구를 보유한 경우에만 상점 카테고리가 해금된다")
    void checksUnlockedCategoryByOwnedBasicFurniture() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'", Long.class);
        Long deskCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '책상'", Long.class);
        Long shopBedId = insertFurniture(bedCategoryId, "해금 확인 침대", "SHOP", 300, true);
        Long shopDeskId = insertFurniture(deskCategoryId, "잠금 확인 책상", "SHOP", 200, true);
        Long basicBedId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = '기본 침대'", Long.class);
        jdbcTemplate.update(
                "INSERT INTO user_furniture (user_id, furniture_id, is_placed) VALUES (?, ?, FALSE)",
                USER_ID, basicBedId);

        assertThat(roomMapper.existsUnlockedCategoryForShopFurniture(USER_ID, shopBedId)).isTrue();
        assertThat(roomMapper.existsUnlockedCategoryForShopFurniture(USER_ID, shopDeskId)).isFalse();
    }

    @Test
    @DisplayName("구매한 상점 가구를 미배치 상태로 한 번만 저장한다")
    void insertsPurchasedFurnitureUnplacedOnlyOnce() {
        Long bedCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM furniture_categories WHERE name = '침대'",
                Long.class);
        Long shopBedId = insertFurniture(bedCategoryId, "모던 침대", "SHOP", 300, true);

        assertThat(roomMapper.insertPurchasedFurniture(USER_ID, shopBedId)).isEqualTo(1);
        assertThat(roomMapper.insertPurchasedFurniture(USER_ID, shopBedId)).isZero();

        RoomFurnitureResponse purchased = roomMapper.findOwnedFurnitureById(USER_ID, shopBedId);
        assertThat(purchased.isPlaced()).isFalse();
    }

    private Long insertFurniture(
            Long categoryId, String name, String furnitureType, int coinPrice, boolean active) {
        jdbcTemplate.update(
                "INSERT INTO furniture "
                        + "(category_id, name, furniture_type, coin_price, asset_url, active) "
                        + "VALUES (?, ?, ?, ?, '/furniture.png', ?)",
                categoryId, name, furnitureType, coinPrice, active);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM furniture WHERE name = ?", Long.class, name);
    }
}

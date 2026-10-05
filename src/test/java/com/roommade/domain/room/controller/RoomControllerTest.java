package com.roommade.domain.room.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.room.dto.response.FurnitureRewardsResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.RoomResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureListResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureResponse;
import com.roommade.domain.room.service.RoomService;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private RoomService roomService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new RoomController(roomService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsRoomWithOwnedFurniture() throws Exception {
        RoomFurnitureResponse furniture = furniture(true);
        when(roomService.getRoom(USER_ID))
                .thenReturn(new RoomResponse(new BigDecimal("30.00"), List.of(furniture)));

        mockMvc.perform(get("/api/rooms").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_001"))
                .andExpect(jsonPath("$.data.readinessScore").value(30.0))
                .andExpect(jsonPath("$.data.furniture[0].assetUrl").value("/bed.png"))
                .andExpect(jsonPath("$.data.furniture[0].placed").value(true));
    }

    @Test
    void returnsPendingFurnitureRewards() throws Exception {
        when(roomService.getPendingRewards(USER_ID))
                .thenReturn(new FurnitureRewardsResponse(List.of()));

        mockMvc.perform(get("/api/rooms/furniture-rewards")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_002"))
                .andExpect(jsonPath("$.data.rewards").isArray());
    }

    @Test
    void claimsFurnitureReward() throws Exception {
        when(roomService.claimReward(eq(USER_ID), eq(100L), eq(10L)))
                .thenReturn(furniture(true));

        mockMvc.perform(post("/api/rooms/furniture-rewards/100/claim")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"furnitureId\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_003"))
                .andExpect(jsonPath("$.data.furnitureId").value(10));
    }

    @Test
    void updatesFurniturePlacement() throws Exception {
        when(roomService.updatePlacement(USER_ID, 10L, false))
                .thenReturn(furniture(false));

        mockMvc.perform(patch("/api/rooms/furniture/10/placement")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placed\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_004"))
                .andExpect(jsonPath("$.data.placed").value(false));
    }

    @Test
    @DisplayName("상점 가구 목록과 카테고리 해금 여부를 조회한다")
    void returnsShopFurnitureList() throws Exception {
        ShopFurnitureResponse desk =
                new ShopFurnitureResponse(
                        30L, 3L, "책상", "웜 오크 책상", "/desk.png", 250, false, true);
        when(roomService.getShopFurniture(USER_ID, 3L)).thenReturn(
                new ShopFurnitureListResponse(List.of(desk)));

        mockMvc.perform(get("/api/rooms/shop/furniture")
                        .param("categoryId", "3")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_012"))
                .andExpect(jsonPath("$.data.furniture[0].coinPrice").value(250))
                .andExpect(jsonPath("$.data.furniture[0].owned").value(false))
                .andExpect(jsonPath("$.data.furniture[0].unlocked").value(true));
    }

    @Test
    @DisplayName("해금한 카테고리의 상점 가구를 구매한다")
    void purchasesFurniture() throws Exception {
        when(roomService.purchaseFurniture(USER_ID, 30L)).thenReturn(furniture(false));

        mockMvc.perform(post("/api/rooms/furniture/30/purchase")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM_009"))
                .andExpect(jsonPath("$.data.furnitureId").value(10));
    }

    @Test
    void rejectsMissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ROOM_005"));

        verifyNoInteractions(roomService);
    }

    @Test
    void rejectsMissingFurnitureId() throws Exception {
        mockMvc.perform(post("/api/rooms/furniture-rewards/100/claim")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors[0].field").value("furnitureId"));

        verifyNoInteractions(roomService);
    }

    private RoomFurnitureResponse furniture(boolean placed) {
        return new RoomFurnitureResponse(
                10L, 2L, "침대", "기본 침대", "/bed.png", placed,
                LocalDateTime.of(2026, 9, 6, 12, 0));
    }
}

package com.roommade.domain.house.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.dto.response.HouseResponse;
import com.roommade.domain.house.service.HouseComparisonService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class HouseComparisonControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private HouseComparisonService houseComparisonService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HouseComparisonController controller = new HouseComparisonController(houseComparisonService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("houseA만 있는 비교 결과를 success=true, comparisonId·houseA 포함 JSON으로 반환하고 houseB는 응답에서 빠진다")
    void returnsCurrentComparisonAsApiResponse() throws Exception {
        HouseResponse houseA = new HouseResponse(
                10L, "서울시 강남구", 100_000_000L, 500_000L, 50_000L,
                null, null, null, null, null, null);
        when(houseComparisonService.getCurrentComparison(eq(USER_ID)))
                .thenReturn(new HouseComparisonCurrentResponse(1L, houseA, null, false));

        mockMvc.perform(get("/api/house-comparisons/current").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.comparisonId").value(1))
                .andExpect(jsonPath("$.data.houseA.id").value(10))
                .andExpect(jsonPath("$.data.houseB").doesNotExist())
                .andExpect(jsonPath("$.data.balanceGameAvailable").value(false));
    }

    @Test
    @DisplayName("비교가 없으면 200과 함께 모든 필드가 null/false인 빈 데이터를 반환한다")
    void returnsEmptyStateWhenNoComparisonExists() throws Exception {
        when(houseComparisonService.getCurrentComparison(eq(USER_ID)))
                .thenReturn(HouseComparisonCurrentResponse.noComparison());

        mockMvc.perform(get("/api/house-comparisons/current").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comparisonId").doesNotExist())
                .andExpect(jsonPath("$.data.houseA").doesNotExist())
                .andExpect(jsonPath("$.data.houseB").doesNotExist())
                .andExpect(jsonPath("$.data.balanceGameAvailable").value(false));
    }

    @Test
    @DisplayName("JSON 요청 바디를 HouseRegisterRequest로 바인딩해 등록하고 201을 반환한다")
    void registersHouseFromJsonRequestBody() throws Exception {
        String requestBody = "{"
                + "\"location\":\"서울시 강남구\","
                + "\"deposit\":100000000,"
                + "\"monthlyRent\":500000,"
                + "\"maintenanceFee\":50000,"
                + "\"area\":29.75,"
                + "\"stationWalkMinutes\":10,"
                + "\"commuteMinutes\":30,"
                + "\"floorType\":\"고층\","
                + "\"roomStructure\":\"원룸\","
                + "\"optionType\":\"풀옵션\"}";
        when(houseComparisonService.registerHouse(eq(USER_ID), eq("A"), any()))
                .thenReturn(new HouseComparisonCurrentResponse(1L, null, null, false));

        mockMvc.perform(post("/api/house-comparisons/current/houses/A")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("HOUSE_002"));

        ArgumentCaptor<HouseRegisterRequest> captor = ArgumentCaptor.forClass(HouseRegisterRequest.class);
        verify(houseComparisonService).registerHouse(eq(USER_ID), eq("A"), captor.capture());
        HouseRegisterRequest captured = captor.getValue();
        assertThat(captured.getLocation()).isEqualTo("서울시 강남구");
        assertThat(captured.getDeposit()).isEqualTo(100_000_000L);
        assertThat(captured.getMonthlyRent()).isEqualTo(500_000L);
        assertThat(captured.getMaintenanceFee()).isEqualTo(50_000L);
        assertThat(captured.getArea()).isEqualByComparingTo(new BigDecimal("29.75"));
        assertThat(captured.getStationWalkMinutes()).isEqualTo(10);
        assertThat(captured.getCommuteMinutes()).isEqualTo(30);
        assertThat(captured.getFloorType()).isEqualTo("고층");
        assertThat(captured.getRoomStructure()).isEqualTo("원룸");
        assertThat(captured.getOptionType()).isEqualTo("풀옵션");
    }

    @Test
    @DisplayName("location/deposit/monthlyRent가 없으면 400을 반환하고 Service를 호출하지 않는다")
    void returnsBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/house-comparisons/current/houses/A")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(houseComparisonService);
    }

    @Test
    @DisplayName("Service가 슬롯 중복 예외를 던지면 409를 반환한다")
    void returnsConflictWhenSlotAlreadyOccupied() throws Exception {
        String requestBody = "{\"location\":\"서울시\",\"deposit\":10000000,\"monthlyRent\":100000}";
        when(houseComparisonService.registerHouse(eq(USER_ID), eq("A"), any()))
                .thenThrow(new BusinessException(HouseErrorCode.HOUSE_SLOT_ALREADY_OCCUPIED));

        mockMvc.perform(post("/api/house-comparisons/current/houses/A")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("HOUSE_004"));
    }
}

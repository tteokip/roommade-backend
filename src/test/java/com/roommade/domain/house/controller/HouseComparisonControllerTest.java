package com.roommade.domain.house.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.dto.response.HouseResponse;
import com.roommade.domain.house.service.HouseComparisonService;
import com.roommade.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
                10L, "서울시 강남구", 10000L, 50L, 5L, null, null, null, null, null, null);
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
}

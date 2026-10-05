package com.roommade.domain.house.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.HouseCommuteEstimateResponse;
import com.roommade.domain.house.service.HouseCommuteService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class HouseCommuteControllerTest {

    private static final Long USER_ID = 1L;
    private static final String LOCATION = "서울시 송파구 방이동";

    @Mock
    private HouseCommuteService houseCommuteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HouseCommuteController controller = new HouseCommuteController(houseCommuteService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("통근 시간 계산 결과를 반환한다")
    void returnsCommuteEstimate() throws Exception {
        when(houseCommuteService.estimate(eq(USER_ID), eq(LOCATION))).thenReturn(
                new HouseCommuteEstimateResponse(LOCATION, 43, 46));

        mockMvc.perform(get("/api/houses/commute-estimate")
                        .header("X-User-Id", USER_ID)
                        .param("location", LOCATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("HOUSE_020"))
                .andExpect(jsonPath("$.data.location").value(LOCATION))
                .andExpect(jsonPath("$.data.commuteMinMinutes").value(43))
                .andExpect(jsonPath("$.data.commuteMaxMinutes").value(46));
    }

    @Test
    @DisplayName("위치를 특정하기 어려우면 성공 응답에 null 통근시간을 반환한다")
    void returnsUnavailableEstimate() throws Exception {
        when(houseCommuteService.estimate(eq(USER_ID), eq("서울 송파구"))).thenReturn(
                new HouseCommuteEstimateResponse("서울 송파구", null, null));

        mockMvc.perform(get("/api/houses/commute-estimate")
                        .header("X-User-Id", USER_ID)
                        .param("location", "서울 송파구"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.location").value("서울 송파구"))
                .andExpect(jsonPath("$.data.commuteMinMinutes").isEmpty())
                .andExpect(jsonPath("$.data.commuteMaxMinutes").isEmpty());
    }

    @Test
    @DisplayName("직장 주소가 없으면 400을 반환한다")
    void returnsBadRequestWhenWorkplaceAddressNotSet() throws Exception {
        when(houseCommuteService.estimate(eq(USER_ID), eq(LOCATION)))
                .thenThrow(new BusinessException(HouseErrorCode.WORKPLACE_ADDRESS_NOT_SET));

        mockMvc.perform(get("/api/houses/commute-estimate")
                        .header("X-User-Id", USER_ID)
                        .param("location", LOCATION))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("HOUSE_021"));
    }

    @Test
    @DisplayName("위치를 특정할 수 없으면 400을 반환한다")
    void returnsBadRequestWhenLocationNotFound() throws Exception {
        when(houseCommuteService.estimate(eq(USER_ID), eq(LOCATION)))
                .thenThrow(new BusinessException(HouseErrorCode.COMMUTE_LOCATION_NOT_FOUND));

        mockMvc.perform(get("/api/houses/commute-estimate")
                        .header("X-User-Id", USER_ID)
                        .param("location", LOCATION))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("HOUSE_022"));
    }

    @Test
    @DisplayName("경로를 찾을 수 없으면 422를 반환한다")
    void returnsUnprocessableEntityWhenRouteNotFound() throws Exception {
        when(houseCommuteService.estimate(eq(USER_ID), eq(LOCATION)))
                .thenThrow(new BusinessException(HouseErrorCode.COMMUTE_ROUTE_NOT_FOUND));

        mockMvc.perform(get("/api/houses/commute-estimate")
                        .header("X-User-Id", USER_ID)
                        .param("location", LOCATION))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("HOUSE_023"));
    }
}

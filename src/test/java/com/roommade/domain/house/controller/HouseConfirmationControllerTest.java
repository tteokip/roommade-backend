package com.roommade.domain.house.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.dto.request.HouseConfirmRequest;
import com.roommade.domain.house.dto.response.HouseConfirmationResponse;
import com.roommade.domain.house.service.HouseConfirmationService;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class HouseConfirmationControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private HouseConfirmationService houseConfirmationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HouseConfirmationController controller =
                new HouseConfirmationController(houseConfirmationService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void confirmsComparisonHouseFromJsonRequest() throws Exception {
        when(houseConfirmationService.confirmHouse(eq(USER_ID), any()))
                .thenReturn(new HouseConfirmationResponse(
                        10L,
                        false,
                        LocalDateTime.of(2026, 8, 30, 20, 0)));

        mockMvc.perform(post("/api/house-confirmations")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmationType\":\"COMPARISON\",\"houseId\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("HOUSE_017"))
                .andExpect(jsonPath("$.data.confirmedHouseId").value(10))
                .andExpect(jsonPath("$.data.manualRentInputRequired").value(false))
                .andExpect(jsonPath("$.data.houseConfirmedAt")
                        .value("2026-08-30T20:00:00"));

        ArgumentCaptor<HouseConfirmRequest> captor =
                ArgumentCaptor.forClass(HouseConfirmRequest.class);
        verify(houseConfirmationService).confirmHouse(eq(USER_ID), captor.capture());
        HouseConfirmRequest captured = captor.getValue();
        assertThat(captured.getConfirmationType())
                .isEqualTo(HouseConfirmRequest.ConfirmationType.COMPARISON);
        assertThat(captured.getHouseId()).isEqualTo(10L);
    }

    @Test
    void confirmsOtherHouseWithoutHouseId() throws Exception {
        when(houseConfirmationService.confirmHouse(eq(USER_ID), any()))
                .thenReturn(new HouseConfirmationResponse(
                        null,
                        true,
                        LocalDateTime.of(2026, 8, 30, 20, 10)));

        mockMvc.perform(post("/api/house-confirmations")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmationType\":\"OTHER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.confirmedHouseId").doesNotExist())
                .andExpect(jsonPath("$.data.manualRentInputRequired").value(true));
    }

    @Test
    void rejectsMissingConfirmationType() throws Exception {
        mockMvc.perform(post("/api/house-confirmations")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors[0].field").value("confirmationType"));

        verifyNoInteractions(houseConfirmationService);
    }
}

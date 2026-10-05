package com.roommade.domain.preparation.controller;

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

import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest;
import com.roommade.domain.preparation.dto.response.MoveInConfirmationResponse;
import com.roommade.domain.preparation.service.MoveInService;
import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class MoveInControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private MoveInService moveInService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MoveInController controller = new MoveInController(moveInService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void confirmsComparisonHouseAndMoveInDate() throws Exception {
        LocalDate moveInDate = LocalDate.of(2026, 9, 15);
        when(moveInService.confirmMoveIn(eq(USER_ID), any()))
                .thenReturn(new MoveInConfirmationResponse(
                        10L,
                        false,
                        moveInDate,
                        null,
                        IndependenceStatus.MOVE_IN_SCHEDULED));

        mockMvc.perform(post("/api/move-ins")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmationType\":\"COMPARISON\",\"houseId\":10,"
                                + "\"moveInDate\":\"2026-09-15\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("PREPARATION_011"))
                .andExpect(jsonPath("$.message").value("입주를 확정했습니다."))
                .andExpect(jsonPath("$.data.confirmedHouseId").value(10))
                .andExpect(jsonPath("$.data.manualRentInputRequired").value(false))
                .andExpect(jsonPath("$.data.moveInDate").value("2026-09-15"))
                .andExpect(jsonPath("$.data.movedInAt").doesNotExist())
                .andExpect(jsonPath("$.data.independenceStatus")
                        .value("MOVE_IN_SCHEDULED"));

        ArgumentCaptor<MoveInConfirmRequest> captor =
                ArgumentCaptor.forClass(MoveInConfirmRequest.class);
        verify(moveInService).confirmMoveIn(eq(USER_ID), captor.capture());
        assertThat(captor.getValue().getMoveInDate()).isEqualTo(moveInDate);
    }

    @Test
    void rejectsMissingMoveInDate() throws Exception {
        mockMvc.perform(post("/api/move-ins")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmationType\":\"OTHER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors[0].field").value("moveInDate"));

        verifyNoInteractions(moveInService);
    }

    @Test
    void rejectsMissingConfirmationType() throws Exception {
        mockMvc.perform(post("/api/move-ins")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"moveInDate\":\"2026-09-15\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors[0].field")
                        .value("confirmationType"));

        verifyNoInteractions(moveInService);
    }
}

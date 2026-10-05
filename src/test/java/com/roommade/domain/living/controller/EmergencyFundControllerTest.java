package com.roommade.domain.living.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import com.roommade.domain.living.service.EmergencyFundService;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class EmergencyFundControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private EmergencyFundService emergencyFundService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EmergencyFundController controller = new EmergencyFundController(emergencyFundService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("비상금 현황을 success=true, LIVING_001 코드로 반환한다")
    void returnsEmergencyFundAsApiResponse() throws Exception {
        when(emergencyFundService.getEmergencyFund(eq(USER_ID)))
                .thenReturn(new EmergencyFundResponse(500_000L, 100_000L, false, null));

        mockMvc.perform(get("/api/living/emergency-funds").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("LIVING_001"))
                .andExpect(jsonPath("$.data.targetAmount").value(500_000))
                .andExpect(jsonPath("$.data.currentAmount").value(100_000))
                .andExpect(jsonPath("$.data.achieved").value(false));
    }

    @Test
    @DisplayName("목표 금액을 설정하면 success=true, LIVING_002 코드로 반환한다")
    void setsTargetAmount() throws Exception {
        LocalDateTime achievedAt = LocalDateTime.of(2026, 8, 29, 0, 0);
        when(emergencyFundService.setTarget(eq(USER_ID), eq(500_000L)))
                .thenReturn(new EmergencyFundResponse(500_000L, 500_000L, true, achievedAt));

        mockMvc.perform(put("/api/living/emergency-funds/target")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetAmount\":500000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("LIVING_002"))
                .andExpect(jsonPath("$.data.achieved").value(true));
    }

    @Test
    @DisplayName("targetAmount가 없거나 0 이하이면 400을 반환하고 Service를 호출하지 않는다")
    void returnsBadRequestWhenTargetAmountIsInvalid() throws Exception {
        mockMvc.perform(put("/api/living/emergency-funds/target")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetAmount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(emergencyFundService);
    }
}
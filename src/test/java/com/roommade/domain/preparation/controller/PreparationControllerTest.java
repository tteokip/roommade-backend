package com.roommade.domain.preparation.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse.Status;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class PreparationControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private PreparationService preparationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PreparationController controller = new PreparationController(preparationService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("RIR 진단 결과를 ApiResponse 형식으로 반환한다")
    void returnsRirDiagnosisAsApiResponse() throws Exception {
        RirDiagnosisResponse response = new RirDiagnosisResponse(
                1_870_000L,
                650_000L,
                new BigDecimal("34.76"),
                new BigDecimal("76.20"),
                new BigDecimal("34.29"),
                45,
                30,
                Status.EXCESSIVE,
                561_000L,
                89_000L);
        when(preparationService.getRirDiagnosis(eq(USER_ID))).thenReturn(response);

        mockMvc.perform(get("/api/preparations/rir").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("PREPARATION_001"))
                .andExpect(jsonPath("$.data.monthlyIncomeWon").value(1_870_000))
                .andExpect(jsonPath("$.data.expectedMonthlyRentWon").value(650_000))
                .andExpect(jsonPath("$.data.rirPercent").value(34.76))
                .andExpect(jsonPath("$.data.achievementRate").value(76.20))
                .andExpect(jsonPath("$.data.score").value(34.29))
                .andExpect(jsonPath("$.data.maxScore").value(45))
                .andExpect(jsonPath("$.data.targetRirPercent").value(30))
                .andExpect(jsonPath("$.data.status").value("EXCESSIVE"))
                .andExpect(jsonPath("$.data.targetMonthlyRentWon").value(561_000))
                .andExpect(jsonPath("$.data.requiredRentReductionWon").value(89_000));
    }

    @Test
    @DisplayName("RIR 계산 데이터가 유효하지 않으면 422 오류 응답을 반환한다")
    void returnsUnprocessableEntityWhenRirIsNotCalculable() throws Exception {
        when(preparationService.getRirDiagnosis(eq(USER_ID)))
                .thenThrow(new BusinessException(PreparationErrorCode.RIR_NOT_CALCULABLE));

        mockMvc.perform(get("/api/preparations/rir").header("X-User-Id", USER_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("PREPARATION_003"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

package com.roommade.domain.coin.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.coin.dto.response.CoinBalanceResponse;
import com.roommade.domain.coin.service.CoinService;
import com.roommade.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class CoinControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CoinService coinService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new CoinController(coinService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsCoinBalanceAsApiResponse() throws Exception {
        when(coinService.getBalance(USER_ID)).thenReturn(new CoinBalanceResponse(150));

        mockMvc.perform(get("/api/coins/balance").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("COIN_001"))
                .andExpect(jsonPath("$.data.balance").value(150));

        verify(coinService).getBalance(USER_ID);
    }

    @Test
    void rejectsMissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/api/coins/balance"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COIN_002"));

        verifyNoInteractions(coinService);
    }
}

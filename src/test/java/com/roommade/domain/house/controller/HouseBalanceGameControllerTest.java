package com.roommade.domain.house.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.BalanceGameProgressResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionsResponse;
import com.roommade.domain.house.dto.response.BalanceGameResultResponse;
import com.roommade.domain.house.dto.response.ComparisonFactor;
import com.roommade.domain.house.service.HouseBalanceGameService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class HouseBalanceGameControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private HouseBalanceGameService houseBalanceGameService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HouseBalanceGameController controller = new HouseBalanceGameController(houseBalanceGameService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("질문 목록과 진행률을 조회한다")
    void returnsQuestions() throws Exception {
        BalanceGameQuestionsResponse response = new BalanceGameQuestionsResponse(
                5, 1, false,
                List.of(new BalanceGameQuestionResponse(
                        1L,
                        "월세·관리비 부담이 적은 집",
                        ComparisonFactor.MONTHLY_COST,
                        "직장까지 가까운 집",
                        ComparisonFactor.COMMUTE,
                        "A")));
        when(houseBalanceGameService.getQuestions(eq(USER_ID))).thenReturn(response);

        mockMvc.perform(get("/api/house-comparisons/current/balance-game/questions")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalQuestions").value(5))
                .andExpect(jsonPath("$.data.answeredQuestions").value(1))
                .andExpect(jsonPath("$.data.completed").value(false))
                .andExpect(jsonPath("$.data.questions[0].questionId").value(1))
                .andExpect(jsonPath("$.data.questions[0].optionAFactor").value("MONTHLY_COST"))
                .andExpect(jsonPath("$.data.questions[0].optionBFactor").value("COMMUTE"))
                .andExpect(jsonPath("$.data.questions[0].selectedSide").value("A"));
    }

    @Test
    @DisplayName("답변을 등록·변경하고 진행률을 반환한다")
    void submitsAnswer() throws Exception {
        when(houseBalanceGameService.submitAnswer(eq(USER_ID), eq(1L), eq("A")))
                .thenReturn(new BalanceGameProgressResponse(5, 2, false));

        mockMvc.perform(put("/api/house-comparisons/current/balance-game/answers/1")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedSide\":\"A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answeredQuestions").value(2));
    }

    @Test
    @DisplayName("결과를 조회한다")
    void returnsResult() throws Exception {
        BalanceGameResultResponse response = new BalanceGameResultResponse(
                "A", 3, 1,
                Map.of(ComparisonFactor.MONTHLY_COST, 2, ComparisonFactor.STATION, 1),
                Map.of(ComparisonFactor.MONTHLY_COST, 2, ComparisonFactor.STATION, 1),
                List.of(ComparisonFactor.AREA));
        when(houseBalanceGameService.getResult(eq(USER_ID))).thenReturn(response);

        mockMvc.perform(get("/api/house-comparisons/current/balance-game/result")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").value("A"))
                .andExpect(jsonPath("$.data.houseAScore").value(3))
                .andExpect(jsonPath("$.data.houseBScore").value(1))
                .andExpect(jsonPath("$.data.selectedFactors.MONTHLY_COST").value(2))
                .andExpect(jsonPath("$.data.matchedFactors.MONTHLY_COST").value(2))
                .andExpect(jsonPath("$.data.matchedFactors.STATION").value(1))
                .andExpect(jsonPath("$.data.excludedFactors[0]").value("AREA"));
    }

    @Test
    @DisplayName("selectedSide가 A/B가 아니면 400을 반환하고 Service를 호출하지 않는다")
    void returnsBadRequestWhenSelectedSideIsInvalid() throws Exception {
        mockMvc.perform(put("/api/house-comparisons/current/balance-game/answers/1")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedSide\":\"C\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(houseBalanceGameService);
    }

    @Test
    @DisplayName("A/B 매물이 모두 등록되지 않았으면 400을 반환한다")
    void returnsBadRequestWhenHousePairIsNotReady() throws Exception {
        when(houseBalanceGameService.getQuestions(eq(USER_ID)))
                .thenThrow(new BusinessException(HouseErrorCode.HOUSE_PAIR_NOT_READY));

        mockMvc.perform(get("/api/house-comparisons/current/balance-game/questions")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("HOUSE_013"));
    }

    @Test
    @DisplayName("현재 출제 대상이 아닌 질문이면 400을 반환한다")
    void returnsBadRequestWhenQuestionNotServed() throws Exception {
        when(houseBalanceGameService.submitAnswer(eq(USER_ID), any(), eq("A")))
                .thenThrow(new BusinessException(HouseErrorCode.BALANCE_GAME_QUESTION_NOT_SERVED));

        mockMvc.perform(put("/api/house-comparisons/current/balance-game/answers/999")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedSide\":\"A\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("HOUSE_015"));
    }

    @Test
    @DisplayName("응답하지 않은 질문이 있으면 결과 조회는 400을 반환한다")
    void returnsBadRequestWhenResultIsIncomplete() throws Exception {
        when(houseBalanceGameService.getResult(eq(USER_ID)))
                .thenThrow(new BusinessException(HouseErrorCode.BALANCE_GAME_INCOMPLETE));

        mockMvc.perform(get("/api/house-comparisons/current/balance-game/result")
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("HOUSE_016"));
    }
}

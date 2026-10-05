package com.roommade.domain.house.controller;

import com.roommade.domain.house.code.HouseSuccessCode;
import com.roommade.domain.house.dto.request.BalanceGameAnswerRequest;
import com.roommade.domain.house.dto.response.BalanceGameProgressResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionsResponse;
import com.roommade.domain.house.dto.response.BalanceGameResultResponse;
import com.roommade.domain.house.service.HouseBalanceGameService;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-comparisons/current/balance-game")
public class HouseBalanceGameController {

    private final HouseBalanceGameService houseBalanceGameService;

    @GetMapping("/questions")
    public ApiResponse<BalanceGameQuestionsResponse> getQuestions(@RequestHeader("X-User-Id") Long userId) {
        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(userId);
        return ApiResponse.success(HouseSuccessCode.BALANCE_GAME_QUESTIONS_FOUND, response);
    }

    @PutMapping("/answers/{questionId}")
    public ApiResponse<BalanceGameProgressResponse> submitAnswer(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long questionId,
            @Valid @RequestBody BalanceGameAnswerRequest request) {
        BalanceGameProgressResponse response =
                houseBalanceGameService.submitAnswer(userId, questionId, request.getSelectedSide());
        return ApiResponse.success(HouseSuccessCode.BALANCE_GAME_ANSWER_SAVED, response);
    }

    @GetMapping("/result")
    public ApiResponse<BalanceGameResultResponse> getResult(@RequestHeader("X-User-Id") Long userId) {
        BalanceGameResultResponse response = houseBalanceGameService.getResult(userId);
        return ApiResponse.success(HouseSuccessCode.BALANCE_GAME_RESULT_FOUND, response);
    }
}

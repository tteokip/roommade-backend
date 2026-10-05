package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.response.BalanceGameProgressResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionsResponse;
import com.roommade.domain.house.dto.response.BalanceGameResultResponse;

public interface HouseBalanceGameService {

    BalanceGameQuestionsResponse getQuestions(Long userId);

    BalanceGameProgressResponse submitAnswer(Long userId, Long questionId, String selectedSide);

    BalanceGameResultResponse getResult(Long userId);
}

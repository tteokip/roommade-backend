package com.roommade.domain.preparation.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositProgressResponse {

    private Long targetDepositWon;
    private Long currentDepositWon;
    private BigDecimal achievementRate;
    private BigDecimal score;
    private Integer maxScore;
    private Long remainingDepositWon;
}

package com.roommade.domain.living.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeLevelResponse {

    private Integer level;
    private Long maxSpending;
    private Integer rewardCoin;
}

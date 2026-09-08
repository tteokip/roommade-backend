package com.roommade.domain.living.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRewardResponse {

    private Long userId;
    private Integer rewardCoin;
}

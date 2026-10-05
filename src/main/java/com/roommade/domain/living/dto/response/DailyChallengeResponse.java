package com.roommade.domain.living.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyChallengeResponse {

    private Long todaySpending;
    private ChallengeLevelResponse currentLevel;
    private List<ChallengeLevelResponse> levels;
}

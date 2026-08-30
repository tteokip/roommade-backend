package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.DailyChallengeResponse;

public interface DailyChallengeService {

    DailyChallengeResponse getDailyChallenge(Long userId);
}

package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.DailyChallengeResponse;

public interface DailyChallengeService {

    DailyChallengeResponse getDailyChallenge(Long userId);

    /** 마감 안 된 지난 날짜의 챌린지를 확정하고 달성한 유저에게 코인을 지급한다. */
    int closeDueChallenges();
}

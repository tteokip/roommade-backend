package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.DailyChallengeResponse;
import com.roommade.domain.living.dto.response.LatestChallengeResultResponse;

public interface DailyChallengeService {

    DailyChallengeResponse getDailyChallenge(Long userId);

    /** 마감 안 된 지난 날짜의 챌린지를 확정하고 달성한 유저에게 코인을 지급한다. */
    int closeDueChallenges();

    /** 가장 최근 마감된 챌린지 결과를 조회한다. 마감 기록이 없으면 null. */
    LatestChallengeResultResponse getLatestResult(Long userId);
}

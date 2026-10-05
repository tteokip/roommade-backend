package com.roommade.domain.living.service;

import com.roommade.domain.coin.service.CoinService;
import com.roommade.domain.living.dto.response.ChallengeLevelResponse;
import com.roommade.domain.living.dto.response.ChallengeRewardResponse;
import com.roommade.domain.living.dto.response.DailyChallengeResponse;
import com.roommade.domain.living.dto.response.LatestChallengeResultResponse;
import com.roommade.domain.living.mapper.ChallengeMapper;
import com.roommade.domain.living.mapper.LivingCostMapper;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyChallengeServiceImpl implements DailyChallengeService {

    private final LivingCostMapper livingCostMapper;
    private final ChallengeMapper challengeMapper;
    private final CoinService coinService;
    private final Clock clock;

    @Override
    public DailyChallengeResponse getDailyChallenge(Long userId) {
        LocalDate today = LocalDate.now(clock);
        Long todaySpending = livingCostMapper.sumDailyCostsBetween(userId, today, today);

        List<ChallengeLevelResponse> levels = challengeMapper.findAllLevels();
        ChallengeLevelResponse currentLevel = levels.stream()
                .filter(level -> level.getMaxSpending() >= todaySpending)
                .findFirst()
                .orElse(null);

        return new DailyChallengeResponse(todaySpending, currentLevel, levels);
    }

    @Override
    @Transactional
    public int closeDueChallenges() {
        LocalDate today = LocalDate.now(clock);
        // closed_at 컬럼이 초 단위(DATETIME)라, 나노초까지 포함된 값으로 저장 후 비교하면
        // INSERT 시점엔 초 단위로 잘려 저장되지만 SELECT 비교값은 그대로라 매칭이 안 된다.
        LocalDateTime closedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        int closedCount = challengeMapper.closeDueChallenges(today, closedAt);
        List<ChallengeRewardResponse> rewards = challengeMapper.findRewardsClosedAt(closedAt);
        for (ChallengeRewardResponse reward : rewards) {
            coinService.earn(reward.getUserId(), reward.getRewardCoin());
        }

        return closedCount;
    }

    @Override
    public LatestChallengeResultResponse getLatestResult(Long userId) {
        return challengeMapper.findLatestResult(userId);
    }
}

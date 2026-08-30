package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.ChallengeLevelResponse;
import com.roommade.domain.living.dto.response.DailyChallengeResponse;
import com.roommade.domain.living.mapper.ChallengeMapper;
import com.roommade.domain.living.mapper.LivingCostMapper;
import java.time.LocalDate;
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

    @Override
    public DailyChallengeResponse getDailyChallenge(Long userId) {
        LocalDate today = LocalDate.now();
        Long todaySpending = livingCostMapper.sumDailyCostsBetween(userId, today, today);

        List<ChallengeLevelResponse> levels = challengeMapper.findAllLevels();
        ChallengeLevelResponse currentLevel = levels.stream()
                .filter(level -> level.getMaxSpending() >= todaySpending)
                .findFirst()
                .orElse(null);

        return new DailyChallengeResponse(todaySpending, currentLevel, levels);
    }
}

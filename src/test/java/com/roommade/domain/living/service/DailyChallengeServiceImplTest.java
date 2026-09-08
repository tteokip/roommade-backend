package com.roommade.domain.living.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.coin.service.CoinService;
import com.roommade.domain.living.dto.response.ChallengeLevelResponse;
import com.roommade.domain.living.dto.response.ChallengeRewardResponse;
import com.roommade.domain.living.dto.response.DailyChallengeResponse;
import com.roommade.domain.living.mapper.ChallengeMapper;
import com.roommade.domain.living.mapper.LivingCostMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyChallengeServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Instant FIXED_INSTANT = Instant.parse("2026-09-08T03:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 8);

    @Mock
    private LivingCostMapper livingCostMapper;

    @Mock
    private ChallengeMapper challengeMapper;

    @Mock
    private CoinService coinService;

    @Mock
    private Clock clock;

    @InjectMocks
    private DailyChallengeServiceImpl dailyChallengeService;

    @BeforeEach
    void setUpClock() {
        when(clock.instant()).thenReturn(FIXED_INSTANT);
        when(clock.getZone()).thenReturn(KOREA_ZONE_ID);
    }

    @Test
    void returnsCurrentLevelBasedOnTodaySpending() {
        List<ChallengeLevelResponse> levels = List.of(
                new ChallengeLevelResponse(3, 10_000L, 50),
                new ChallengeLevelResponse(2, 15_000L, 35),
                new ChallengeLevelResponse(1, 30_000L, 20));
        when(livingCostMapper.sumDailyCostsBetween(USER_ID, TODAY, TODAY)).thenReturn(12_000L);
        when(challengeMapper.findAllLevels()).thenReturn(levels);

        DailyChallengeResponse response = dailyChallengeService.getDailyChallenge(USER_ID);

        assertThat(response.getTodaySpending()).isEqualTo(12_000L);
        assertThat(response.getCurrentLevel().getLevel()).isEqualTo(2);
    }

    @Test
    void returnsNullLevelWhenSpendingExceedsAllLevels() {
        List<ChallengeLevelResponse> levels = List.of(
                new ChallengeLevelResponse(3, 10_000L, 50),
                new ChallengeLevelResponse(2, 15_000L, 35),
                new ChallengeLevelResponse(1, 30_000L, 20));
        when(livingCostMapper.sumDailyCostsBetween(USER_ID, TODAY, TODAY)).thenReturn(50_000L);
        when(challengeMapper.findAllLevels()).thenReturn(levels);

        DailyChallengeResponse response = dailyChallengeService.getDailyChallenge(USER_ID);

        assertThat(response.getCurrentLevel()).isNull();
    }

    @Test
    void grantsCoinsForEachRewardedUserOnClose() {
        when(challengeMapper.closeDueChallenges(eq(TODAY), any())).thenReturn(3);
        when(challengeMapper.findRewardsClosedAt(any())).thenReturn(List.of(
                new ChallengeRewardResponse(10L, 50),
                new ChallengeRewardResponse(20L, 20)));

        int closedCount = dailyChallengeService.closeDueChallenges();

        assertThat(closedCount).isEqualTo(3);
        verify(coinService).earn(10L, 50);
        verify(coinService).earn(20L, 20);
    }

    @Test
    void grantsNothingWhenNoOneAchievedALevel() {
        when(challengeMapper.closeDueChallenges(eq(TODAY), any())).thenReturn(2);
        when(challengeMapper.findRewardsClosedAt(any())).thenReturn(List.of());

        int closedCount = dailyChallengeService.closeDueChallenges();

        assertThat(closedCount).isEqualTo(2);
        verify(coinService, never()).earn(any(), anyInt());
    }
}

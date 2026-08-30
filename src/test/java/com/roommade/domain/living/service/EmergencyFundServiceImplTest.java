package com.roommade.domain.living.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import com.roommade.domain.living.mapper.EmergencyFundMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmergencyFundServiceImplTest {

    @Mock
    private EmergencyFundMapper emergencyFundMapper;

    @InjectMocks
    private EmergencyFundServiceImpl emergencyFundService;

    @Test
    @DisplayName("Mapper 결과가 있으면 가공 없이 그대로 반환한다")
    void returnsMapperResultWhenRowExists() {
        Long userId = 1L;
        EmergencyFundResponse mapperResult = new EmergencyFundResponse(500_000L, 100_000L, false, null);
        when(emergencyFundMapper.findByUserId(userId)).thenReturn(mapperResult);

        EmergencyFundResponse result = emergencyFundService.getEmergencyFund(userId);

        assertThat(result).isSameAs(mapperResult);
    }

    @Test
    @DisplayName("Mapper가 null을 반환하면 0/0/false/null인 notStarted 응답을 반환한다")
    void returnsNotStartedWhenMapperReturnsNull() {
        Long userId = 1L;
        when(emergencyFundMapper.findByUserId(userId)).thenReturn(null);

        EmergencyFundResponse result = emergencyFundService.getEmergencyFund(userId);

        assertThat(result.getTargetAmount()).isZero();
        assertThat(result.getCurrentAmount()).isZero();
        assertThat(result.isAchieved()).isFalse();
        assertThat(result.getAchievedAt()).isNull();
    }

    @Test
    @DisplayName("행이 없으면 목표 금액으로 새 행을 생성하고, current_amount가 0이라 achieved_at은 null이다")
    void insertsNewRowWithNullAchievedAtWhenNoneExists() {
        Long userId = 1L;
        when(emergencyFundMapper.findByUserId(userId))
                .thenReturn(null)
                .thenReturn(new EmergencyFundResponse(500_000L, 0L, false, null));

        emergencyFundService.setTarget(userId, 500_000L);

        verify(emergencyFundMapper).insert(userId, 500_000L, null);
    }

    @Test
    @DisplayName("행이 있고 achieved_at이 없으면 target 갱신 시 그대로 achieved_at 없이 갱신한다")
    void updatesTargetKeepingNullAchievedAtWhenNotYetAchieved() {
        Long userId = 1L;
        EmergencyFundResponse current = new EmergencyFundResponse(500_000L, 100_000L, false, null);
        when(emergencyFundMapper.findByUserId(userId))
                .thenReturn(current)
                .thenReturn(new EmergencyFundResponse(700_000L, 100_000L, false, null));

        emergencyFundService.setTarget(userId, 700_000L);

        verify(emergencyFundMapper).updateTarget(userId, 700_000L, null);
    }

    @Test
    @DisplayName("현재 금액이 새 목표 금액 이상이고 achieved_at이 없었으면 최초 달성 시각을 채워 갱신한다")
    void setsAchievedAtWhenCurrentAmountReachesNewTarget() {
        Long userId = 1L;
        EmergencyFundResponse current = new EmergencyFundResponse(500_000L, 500_000L, false, null);
        when(emergencyFundMapper.findByUserId(userId))
                .thenReturn(current)
                .thenReturn(new EmergencyFundResponse(300_000L, 500_000L, true, LocalDateTime.now()));

        emergencyFundService.setTarget(userId, 300_000L);

        verify(emergencyFundMapper).updateTarget(eq(userId), eq(300_000L), notNull());
    }

    @Test
    @DisplayName("이미 achieved_at이 있으면 목표 금액을 다시 올려도 achieved_at을 지우지 않는다")
    void keepsExistingAchievedAtWhenTargetRaisedAgain() {
        Long userId = 1L;
        LocalDateTime firstAchievedAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        EmergencyFundResponse current = new EmergencyFundResponse(300_000L, 500_000L, true, firstAchievedAt);
        when(emergencyFundMapper.findByUserId(userId))
                .thenReturn(current)
                .thenReturn(new EmergencyFundResponse(800_000L, 500_000L, true, firstAchievedAt));

        emergencyFundService.setTarget(userId, 800_000L);

        verify(emergencyFundMapper).updateTarget(userId, 800_000L, firstAchievedAt);
    }
}
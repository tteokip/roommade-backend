package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.dto.response.HouseComparisonProgressResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseComparisonProgressServiceImplTest {

    @Mock
    private PreparationMapper preparationMapper;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @Test
    @DisplayName("비교 매물 등록 전에는 집 비교 점수 0점과 최대 점수 10점을 반환한다")
    void returnsZeroScoreBeforeHouseRegistration() {
        when(preparationMapper.findHouseComparisonCompletedAtByUserId(1L)).thenReturn(null);

        HouseComparisonProgressResponse response =
                preparationService.getHouseComparisonProgress(1L);

        assertThat(response.getHouseComparisonScore()).isZero();
        assertThat(response.getMaxScore()).isEqualTo(10);
        assertThat(response.getHouseComparisonCompletedAt()).isNull();
    }

    @Test
    @DisplayName("비교 매물 등록 후에는 집 비교 점수 10점과 완료 시간을 반환한다")
    void returnsFullScoreAfterHouseRegistration() {
        LocalDateTime completedAt = LocalDateTime.of(2026, 8, 30, 16, 30);
        when(preparationMapper.findHouseComparisonCompletedAtByUserId(1L))
                .thenReturn(completedAt);

        HouseComparisonProgressResponse response =
                preparationService.getHouseComparisonProgress(1L);

        assertThat(response.getHouseComparisonScore()).isEqualTo(10);
        assertThat(response.getMaxScore()).isEqualTo(10);
        assertThat(response.getHouseComparisonCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    @DisplayName("최초 비교 매물 등록 완료 기록을 Mapper에 위임한다")
    void marksHouseComparisonCompleted() {
        preparationService.markHouseComparisonCompleted(1L);

        verify(preparationMapper).markHouseComparisonCompleted(1L);
    }
}

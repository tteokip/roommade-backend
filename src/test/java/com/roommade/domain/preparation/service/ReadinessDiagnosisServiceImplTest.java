package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadinessDiagnosisServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private PreparationMapper preparationMapper;

    @Spy
    private RirCalculator rirCalculator;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @Test
    @DisplayName("RIR·보증금·집 비교 점수와 각 최대 점수를 합산한다")
    void calculatesReadinessDiagnosis() {
        when(preparationMapper.findRirProfileByUserId(USER_ID))
                .thenReturn(new RirProfileResponse(1_870_000L, 650_000L));
        when(preparationMapper.findDepositProgressByUserId(USER_ID))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 35_123_456L));
        when(preparationMapper.findHouseComparisonCompletedAtByUserId(USER_ID))
                .thenReturn(LocalDateTime.of(2026, 8, 30, 16, 30));
        LocalDate moveInDate = LocalDate.of(2026, 9, 15);
        when(preparationMapper.findMoveInStateByUserId(USER_ID))
                .thenReturn(new MoveInStateSourceResponse(moveInDate, null));

        ReadinessDiagnosisResponse response =
                preparationService.getReadinessDiagnosis(USER_ID);

        assertThat(response.getReadinessScore()).isEqualByComparingTo("75.90");
        assertThat(response.getMaxScore()).isEqualTo(100);
        assertThat(response.getRirScore()).isEqualByComparingTo("34.29");
        assertThat(response.getRirMaxScore()).isEqualTo(45);
        assertThat(response.getDepositScore()).isEqualByComparingTo("31.61");
        assertThat(response.getDepositMaxScore()).isEqualTo(45);
        assertThat(response.getHouseComparisonScore()).isEqualTo(10);
        assertThat(response.getHouseComparisonMaxScore()).isEqualTo(10);
        assertThat(response.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(response.getIndependenceStatus())
                .isEqualTo(IndependenceStatus.MOVE_IN_SCHEDULED);
    }

    @Test
    @DisplayName("집 비교 미완료 시 집 비교 점수 0점을 합산한다")
    void calculatesReadinessWithoutHouseComparisonScore() {
        when(preparationMapper.findRirProfileByUserId(USER_ID))
                .thenReturn(new RirProfileResponse(2_000_000L, 600_000L));
        when(preparationMapper.findDepositProgressByUserId(USER_ID))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 50_000_000L));
        when(preparationMapper.findHouseComparisonCompletedAtByUserId(USER_ID)).thenReturn(null);
        when(preparationMapper.findMoveInStateByUserId(USER_ID)).thenReturn(null);

        ReadinessDiagnosisResponse response =
                preparationService.getReadinessDiagnosis(USER_ID);

        assertThat(response.getReadinessScore()).isEqualByComparingTo("90.00");
        assertThat(response.getHouseComparisonScore()).isZero();
        assertThat(response.getHouseComparisonMaxScore()).isEqualTo(10);
        assertThat(response.getIndependenceStatus())
                .isEqualTo(IndependenceStatus.PREPARING);
    }

    @Test
    @DisplayName("집 확정 완료 시 구성 점수는 유지하고 전체 자립 준비도만 100점으로 반환한다")
    void returnsFullReadinessScoreAfterMoveIn() {
        when(preparationMapper.findRirProfileByUserId(USER_ID))
                .thenReturn(new RirProfileResponse(1_870_000L, 650_000L));
        when(preparationMapper.findDepositProgressByUserId(USER_ID))
                .thenReturn(new DepositProgressSourceResponse(50_000_000L, 0L));
        when(preparationMapper.findHouseComparisonCompletedAtByUserId(USER_ID)).thenReturn(null);
        LocalDate moveInDate = LocalDate.of(2026, 8, 30);
        LocalDateTime movedInAt = LocalDateTime.of(2026, 8, 30, 18, 0);
        when(preparationMapper.findMoveInStateByUserId(USER_ID))
                .thenReturn(new MoveInStateSourceResponse(moveInDate, movedInAt));

        ReadinessDiagnosisResponse response =
                preparationService.getReadinessDiagnosis(USER_ID);

        assertThat(response.getReadinessScore()).isEqualByComparingTo("100.00");
        assertThat(response.getMaxScore()).isEqualTo(100);
        assertThat(response.getRirScore()).isEqualByComparingTo("34.29");
        assertThat(response.getDepositScore()).isEqualByComparingTo("0.00");
        assertThat(response.getHouseComparisonScore()).isZero();
        assertThat(response.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(response.getMovedInAt()).isEqualTo(movedInAt);
        assertThat(response.getIndependenceStatus())
                .isEqualTo(IndependenceStatus.MOVED_IN);
    }
}

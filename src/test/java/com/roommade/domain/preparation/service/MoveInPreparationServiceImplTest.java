package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import com.roommade.global.exception.BusinessException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoveInPreparationServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long HOUSE_ID = 10L;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Instant FIXED_INSTANT = Instant.parse("2026-09-02T03:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 2);

    @Mock
    private PreparationMapper preparationMapper;

    @Mock
    private Clock clock;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @BeforeEach
    void setUpClock() {
        when(clock.instant()).thenReturn(FIXED_INSTANT);
        when(clock.getZone()).thenReturn(KOREA_ZONE_ID);
    }

    @Test
    void schedulesFutureMoveInWithoutTransitionTime() {
        LocalDate moveInDate = TODAY.plusDays(7);
        MoveInStateSourceResponse state =
                new MoveInStateSourceResponse(moveInDate, null);
        when(preparationMapper.updateMoveInSchedule(
                USER_ID, HOUSE_ID, moveInDate, null)).thenReturn(1);
        when(preparationMapper.findMoveInStateByUserId(USER_ID)).thenReturn(state);

        MoveInStateSourceResponse result =
                preparationService.scheduleMoveIn(USER_ID, HOUSE_ID, moveInDate);

        assertThat(result).isSameAs(state);
    }

    @Test
    void transitionsImmediatelyWhenMoveInDateIsToday() {
        LocalDate moveInDate = TODAY;
        when(preparationMapper.updateMoveInSchedule(
                eq(USER_ID), eq(HOUSE_ID), eq(moveInDate),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(1);
        when(preparationMapper.findMoveInStateByUserId(USER_ID))
                .thenReturn(new MoveInStateSourceResponse(
                        moveInDate, LocalDateTime.of(2026, 9, 2, 12, 0)));

        preparationService.scheduleMoveIn(USER_ID, HOUSE_ID, moveInDate);

        ArgumentCaptor<LocalDateTime> movedInAtCaptor =
                ArgumentCaptor.forClass(LocalDateTime.class);
        verify(preparationMapper).updateMoveInSchedule(
                eq(USER_ID), eq(HOUSE_ID), eq(moveInDate), movedInAtCaptor.capture());
        assertThat(movedInAtCaptor.getValue().toLocalDate()).isEqualTo(moveInDate);
    }

    @Test
    void rejectsRepeatedMoveInConfirmation() {
        LocalDate moveInDate = TODAY.plusDays(7);
        when(preparationMapper.updateMoveInSchedule(
                USER_ID, HOUSE_ID, moveInDate, null)).thenReturn(0);
        when(preparationMapper.existsIndependenceProgressByUserId(USER_ID)).thenReturn(true);

        assertThatThrownBy(() ->
                preparationService.scheduleMoveIn(USER_ID, HOUSE_ID, moveInDate))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.MOVE_IN_ALREADY_CONFIRMED);
    }

    @Test
    void rejectsMoveInWithoutIndependenceProgress() {
        LocalDate moveInDate = TODAY.plusDays(7);
        when(preparationMapper.updateMoveInSchedule(
                USER_ID, null, moveInDate, null)).thenReturn(0);
        when(preparationMapper.existsIndependenceProgressByUserId(USER_ID)).thenReturn(false);

        assertThatThrownBy(() ->
                preparationService.scheduleMoveIn(USER_ID, null, moveInDate))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.INDEPENDENCE_PROGRESS_NOT_FOUND);
    }

    @Test
    void transitionsDueMoveInsUsingKoreaDate() {
        when(preparationMapper.updateDueMoveIns(
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(2);

        int updatedRows = preparationService.transitionDueMoveIns();

        assertThat(updatedRows).isEqualTo(2);
        ArgumentCaptor<LocalDate> todayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(preparationMapper).updateDueMoveIns(
                todayCaptor.capture(),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class));
        assertThat(todayCaptor.getValue()).isEqualTo(TODAY);
    }
}

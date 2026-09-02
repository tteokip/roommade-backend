package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest;
import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest.ConfirmationType;
import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.domain.preparation.dto.response.MoveInConfirmationResponse;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.global.exception.BusinessException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoveInServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long HOUSE_ID = 10L;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Instant FIXED_INSTANT = Instant.parse("2026-09-02T03:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 2);

    @Mock
    private HouseComparisonMapper houseComparisonMapper;

    @Mock
    private PreparationService preparationService;

    @Mock
    private Clock clock;

    @InjectMocks
    private MoveInServiceImpl moveInService;

    @BeforeEach
    void setUpClock() {
        when(clock.instant()).thenReturn(FIXED_INSTANT);
        when(clock.getZone()).thenReturn(KOREA_ZONE_ID);
    }

    @Test
    void schedulesOwnedComparisonHouseMoveIn() {
        LocalDate moveInDate = TODAY.plusDays(7);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.COMPARISON, HOUSE_ID, moveInDate);
        when(houseComparisonMapper.existsHouseByIdAndUserId(HOUSE_ID, USER_ID))
                .thenReturn(true);
        when(preparationService.scheduleMoveIn(USER_ID, HOUSE_ID, moveInDate))
                .thenReturn(new MoveInStateSourceResponse(moveInDate, null));

        MoveInConfirmationResponse result = moveInService.confirmMoveIn(USER_ID, request);

        assertThat(result.getConfirmedHouseId()).isEqualTo(HOUSE_ID);
        assertThat(result.isManualRentInputRequired()).isFalse();
        assertThat(result.getMoveInDate()).isEqualTo(moveInDate);
        assertThat(result.getMovedInAt()).isNull();
        assertThat(result.getIndependenceStatus())
                .isEqualTo(IndependenceStatus.MOVE_IN_SCHEDULED);
    }

    @Test
    void schedulesOtherHouseWithoutRegisteredHouseLookup() {
        LocalDate moveInDate = TODAY.plusDays(7);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.OTHER, null, moveInDate);
        when(preparationService.scheduleMoveIn(USER_ID, null, moveInDate))
                .thenReturn(new MoveInStateSourceResponse(moveInDate, null));

        MoveInConfirmationResponse result = moveInService.confirmMoveIn(USER_ID, request);

        assertThat(result.getConfirmedHouseId()).isNull();
        assertThat(result.isManualRentInputRequired()).isTrue();
        verifyNoInteractions(houseComparisonMapper);
    }

    @Test
    void returnsMovedInStatusForTodayMoveIn() {
        LocalDate moveInDate = TODAY;
        LocalDateTime movedInAt = LocalDateTime.of(2026, 9, 2, 12, 0);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.OTHER, null, moveInDate);
        when(preparationService.scheduleMoveIn(USER_ID, null, moveInDate))
                .thenReturn(new MoveInStateSourceResponse(moveInDate, movedInAt));

        MoveInConfirmationResponse result = moveInService.confirmMoveIn(USER_ID, request);

        assertThat(result.getMovedInAt()).isEqualTo(movedInAt);
        assertThat(result.getIndependenceStatus()).isEqualTo(IndependenceStatus.MOVED_IN);
    }

    @Test
    void rejectsPastMoveInDate() {
        LocalDate moveInDate = TODAY.minusDays(1);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.OTHER, null, moveInDate);

        assertThatThrownBy(() -> moveInService.confirmMoveIn(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.MOVE_IN_DATE_IN_PAST);

        verifyNoInteractions(houseComparisonMapper, preparationService);
    }

    @Test
    void rejectsComparisonMoveInWithoutHouseId() {
        LocalDate moveInDate = TODAY.plusDays(1);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.COMPARISON, null, moveInDate);

        assertThatThrownBy(() -> moveInService.confirmMoveIn(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);

        verifyNoInteractions(houseComparisonMapper, preparationService);
    }

    @Test
    void rejectsOtherMoveInWithHouseId() {
        LocalDate moveInDate = TODAY.plusDays(1);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.OTHER, HOUSE_ID, moveInDate);

        assertThatThrownBy(() -> moveInService.confirmMoveIn(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);

        verifyNoInteractions(houseComparisonMapper, preparationService);
    }

    @Test
    void rejectsHouseRegisteredByAnotherUser() {
        LocalDate moveInDate = TODAY.plusDays(1);
        MoveInConfirmRequest request =
                new MoveInConfirmRequest(ConfirmationType.COMPARISON, HOUSE_ID, moveInDate);
        when(houseComparisonMapper.existsHouseByIdAndUserId(HOUSE_ID, USER_ID))
                .thenReturn(false);

        assertThatThrownBy(() -> moveInService.confirmMoveIn(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(HouseErrorCode.HOUSE_NOT_CONFIRMABLE);

        verify(preparationService, never()).scheduleMoveIn(USER_ID, HOUSE_ID, moveInDate);
    }
}

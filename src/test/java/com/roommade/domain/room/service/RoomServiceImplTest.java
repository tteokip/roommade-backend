package com.roommade.domain.room.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.domain.room.code.RoomErrorCode;
import com.roommade.domain.room.dto.response.FurnitureOptionResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardSourceResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.mapper.RoomMapper;
import com.roommade.global.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private PreparationService preparationService;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void synchronizesReachedRewardStagesAndReturnsOwnedFurniture() {
        RoomFurnitureResponse bed = furniture(10L, "기본 침대", true);
        when(preparationService.getReadinessDiagnosis(USER_ID))
                .thenReturn(readiness("30.00", IndependenceStatus.PREPARING));
        when(roomMapper.findOwnedFurnitureByUserId(USER_ID)).thenReturn(List.of(bed));

        assertThat(roomService.getRoom(USER_ID).getFurniture()).containsExactly(bed);

        verify(roomMapper).insertInitialFurniture(USER_ID);
        verify(roomMapper).insertRewardIfAbsent(USER_ID, 15);
        verify(roomMapper).insertRewardIfAbsent(USER_ID, 30);
        verify(roomMapper, never()).insertRewardIfAbsent(USER_ID, 45);
    }

    @Test
    void grantsAllBasicFurnitureAfterMoveInWasScheduled() {
        when(preparationService.getReadinessDiagnosis(USER_ID))
                .thenReturn(readiness("45.00", IndependenceStatus.MOVE_IN_SCHEDULED));
        when(roomMapper.findOwnedFurnitureByUserId(USER_ID)).thenReturn(List.of());

        roomService.getRoom(USER_ID);

        verify(roomMapper).insertInitialFurniture(USER_ID);
        verify(roomMapper).insertAllMissingBasicFurniture(USER_ID);
        verify(roomMapper, never()).insertRewardIfAbsent(USER_ID, 15);
    }

    @Test
    void returnsPendingRewardsWithSelectableFurniture() {
        LocalDateTime grantedAt = LocalDateTime.of(2026, 9, 6, 12, 0);
        FurnitureRewardSourceResponse reward =
                new FurnitureRewardSourceResponse(100L, 15, grantedAt);
        FurnitureOptionResponse option =
                new FurnitureOptionResponse(10L, 2L, "침대", "기본 침대", "/bed.png");
        when(preparationService.getReadinessDiagnosis(USER_ID))
                .thenReturn(readiness("15.00", IndependenceStatus.PREPARING));
        when(roomMapper.findPendingRewardsByUserId(USER_ID)).thenReturn(List.of(reward));
        when(roomMapper.findSelectableFurniture(USER_ID, 15)).thenReturn(List.of(option));

        assertThat(roomService.getPendingRewards(USER_ID).getRewards())
                .singleElement()
                .satisfies(result -> {
                    assertThat(result.getRewardId()).isEqualTo(100L);
                    assertThat(result.getChoices()).containsExactly(option);
                });
    }

    @Test
    void claimsSelectableFurnitureAndPlacesIt() {
        FurnitureRewardSourceResponse reward =
                new FurnitureRewardSourceResponse(100L, 30, LocalDateTime.now());
        RoomFurnitureResponse chair = furniture(20L, "기본 의자", true);
        when(roomMapper.findPendingRewardForUpdate(USER_ID, 100L)).thenReturn(reward);
        when(roomMapper.existsSelectableFurniture(USER_ID, 20L, 30)).thenReturn(true);
        when(roomMapper.insertUserFurniture(USER_ID, 20L)).thenReturn(1);
        when(roomMapper.claimReward(USER_ID, 100L, 20L)).thenReturn(1);
        when(roomMapper.findOwnedFurnitureById(USER_ID, 20L)).thenReturn(chair);

        assertThat(roomService.claimReward(USER_ID, 100L, 20L)).isSameAs(chair);

        verify(roomMapper).unplaceFurnitureInSameCategory(USER_ID, 20L);
    }

    @Test
    void rejectsFurnitureOutsideRewardStage() {
        FurnitureRewardSourceResponse reward =
                new FurnitureRewardSourceResponse(100L, 15, LocalDateTime.now());
        when(roomMapper.findPendingRewardForUpdate(USER_ID, 100L)).thenReturn(reward);

        assertThatThrownBy(() -> roomService.claimReward(USER_ID, 100L, 20L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(RoomErrorCode.FURNITURE_NOT_SELECTABLE);

        verify(roomMapper, never()).insertUserFurniture(USER_ID, 20L);
    }

    @Test
    void replacesPlacedFurnitureWithinSameCategory() {
        RoomFurnitureResponse bed = furniture(10L, "모던 침대", true);
        when(roomMapper.existsOwnedFurniture(USER_ID, 10L)).thenReturn(true);
        when(roomMapper.findOwnedFurnitureById(USER_ID, 10L)).thenReturn(bed);

        assertThat(roomService.updatePlacement(USER_ID, 10L, true)).isSameAs(bed);

        verify(roomMapper).unplaceFurnitureInSameCategory(USER_ID, 10L);
        verify(roomMapper).updateFurniturePlacement(USER_ID, 10L, true);
    }

    @Test
    void rejectsPlacementOfUnownedFurniture() {
        assertThatThrownBy(() -> roomService.updatePlacement(USER_ID, 10L, true))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(RoomErrorCode.FURNITURE_NOT_OWNED);
    }

    private ReadinessDiagnosisResponse readiness(
            String score, IndependenceStatus status) {
        return new ReadinessDiagnosisResponse(
                new BigDecimal(score), 100,
                BigDecimal.ZERO, 45,
                BigDecimal.ZERO, 45,
                0, 10,
                null, null, status);
    }

    private RoomFurnitureResponse furniture(Long furnitureId, String name, boolean placed) {
        return new RoomFurnitureResponse(
                furnitureId, 2L, "침대", name, "/bed.png", placed,
                LocalDateTime.of(2026, 9, 6, 12, 0));
    }
}

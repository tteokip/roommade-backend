package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.request.HouseConfirmRequest;
import com.roommade.domain.house.dto.request.HouseConfirmRequest.ConfirmationType;
import com.roommade.domain.house.dto.response.HouseConfirmationResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseConfirmationServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long HOUSE_ID = 10L;

    @Mock
    private HouseComparisonMapper houseComparisonMapper;

    @Mock
    private PreparationService preparationService;

    @InjectMocks
    private HouseConfirmationServiceImpl houseConfirmationService;

    @Test
    void confirmsOwnedComparisonHouse() {
        LocalDateTime confirmedAt = LocalDateTime.of(2026, 8, 30, 20, 0);
        HouseConfirmRequest request =
                new HouseConfirmRequest(ConfirmationType.COMPARISON, HOUSE_ID);
        when(houseComparisonMapper.existsHouseByIdAndUserId(HOUSE_ID, USER_ID))
                .thenReturn(true);
        when(preparationService.confirmHouse(USER_ID, HOUSE_ID)).thenReturn(confirmedAt);

        HouseConfirmationResponse result =
                houseConfirmationService.confirmHouse(USER_ID, request);

        assertThat(result.getConfirmedHouseId()).isEqualTo(HOUSE_ID);
        assertThat(result.isManualRentInputRequired()).isFalse();
        assertThat(result.getHouseConfirmedAt()).isEqualTo(confirmedAt);
        verify(preparationService).confirmHouse(USER_ID, HOUSE_ID);
    }

    @Test
    void confirmsOtherHouseWithoutRegisteredHouseLookup() {
        LocalDateTime confirmedAt = LocalDateTime.of(2026, 8, 30, 20, 10);
        HouseConfirmRequest request = new HouseConfirmRequest(ConfirmationType.OTHER, null);
        when(preparationService.confirmHouse(USER_ID, null)).thenReturn(confirmedAt);

        HouseConfirmationResponse result =
                houseConfirmationService.confirmHouse(USER_ID, request);

        assertThat(result.getConfirmedHouseId()).isNull();
        assertThat(result.isManualRentInputRequired()).isTrue();
        assertThat(result.getHouseConfirmedAt()).isEqualTo(confirmedAt);
        verifyNoInteractions(houseComparisonMapper);
    }

    @Test
    void rejectsComparisonConfirmationWithoutHouseId() {
        HouseConfirmRequest request =
                new HouseConfirmRequest(ConfirmationType.COMPARISON, null);

        assertThatThrownBy(() -> houseConfirmationService.confirmHouse(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(HouseErrorCode.INVALID_HOUSE_CONFIRMATION);

        verifyNoInteractions(houseComparisonMapper, preparationService);
    }

    @Test
    void rejectsOtherConfirmationWithHouseId() {
        HouseConfirmRequest request = new HouseConfirmRequest(ConfirmationType.OTHER, HOUSE_ID);

        assertThatThrownBy(() -> houseConfirmationService.confirmHouse(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(HouseErrorCode.INVALID_HOUSE_CONFIRMATION);

        verifyNoInteractions(houseComparisonMapper, preparationService);
    }

    @Test
    void rejectsHouseOwnedByAnotherUser() {
        HouseConfirmRequest request =
                new HouseConfirmRequest(ConfirmationType.COMPARISON, HOUSE_ID);
        when(houseComparisonMapper.existsHouseByIdAndUserId(HOUSE_ID, USER_ID))
                .thenReturn(false);

        assertThatThrownBy(() -> houseConfirmationService.confirmHouse(USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(HouseErrorCode.HOUSE_NOT_CONFIRMABLE);

        verify(preparationService, never()).confirmHouse(USER_ID, HOUSE_ID);
    }
}

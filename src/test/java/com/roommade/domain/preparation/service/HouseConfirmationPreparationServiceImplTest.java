package com.roommade.domain.preparation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.mapper.PreparationMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseConfirmationPreparationServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long HOUSE_ID = 10L;

    @Mock
    private PreparationMapper preparationMapper;

    @InjectMocks
    private PreparationServiceImpl preparationService;

    @Test
    void recordsHouseConfirmationOnce() {
        LocalDateTime confirmedAt = LocalDateTime.of(2026, 8, 30, 20, 0);
        when(preparationMapper.updateHouseConfirmation(USER_ID, HOUSE_ID)).thenReturn(1);
        when(preparationMapper.findHouseConfirmedAtByUserId(USER_ID)).thenReturn(confirmedAt);

        LocalDateTime result = preparationService.confirmHouse(USER_ID, HOUSE_ID);

        assertThat(result).isEqualTo(confirmedAt);
        verify(preparationMapper).updateHouseConfirmation(USER_ID, HOUSE_ID);
    }

    @Test
    void rejectsRepeatedHouseConfirmation() {
        when(preparationMapper.updateHouseConfirmation(USER_ID, HOUSE_ID)).thenReturn(0);
        when(preparationMapper.existsIndependenceProgressByUserId(USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> preparationService.confirmHouse(USER_ID, HOUSE_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.HOUSE_ALREADY_CONFIRMED);
    }

    @Test
    void rejectsConfirmationWithoutIndependenceProgress() {
        when(preparationMapper.updateHouseConfirmation(USER_ID, null)).thenReturn(0);
        when(preparationMapper.existsIndependenceProgressByUserId(USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> preparationService.confirmHouse(USER_ID, null))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(PreparationErrorCode.INDEPENDENCE_PROGRESS_NOT_FOUND);
    }
}

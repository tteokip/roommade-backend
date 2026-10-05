package com.roommade.domain.preparation.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.preparation.service.PreparationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndependenceTransitionSchedulerTest {

    @Mock
    private PreparationService preparationService;

    @InjectMocks
    private IndependenceTransitionScheduler scheduler;

    @Test
    void transitionsOnServerStartupAndDailySchedule() {
        when(preparationService.transitionDueMoveIns()).thenReturn(1, 0);

        scheduler.afterSingletonsInstantiated();
        scheduler.transitionDaily();

        verify(preparationService, times(2)).transitionDueMoveIns();
    }
}

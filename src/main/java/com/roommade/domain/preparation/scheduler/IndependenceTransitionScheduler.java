package com.roommade.domain.preparation.scheduler;

import com.roommade.domain.preparation.service.PreparationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndependenceTransitionScheduler implements SmartInitializingSingleton {

    private final PreparationService preparationService;

    /** 서버 시작 시 중단 기간에 누락된 입주 전환을 보정한다. */
    @Override
    public void afterSingletonsInstantiated() {
        transitionDueMoveIns("서버 시작");
    }

    /** 한국 시간 기준 매일 00시 05분에 입주일이 도래한 사용자를 전환한다. */
    @Scheduled(
            cron = "${independence.transition-cron:0 5 0 * * *}",
            zone = "${independence.transition-zone:Asia/Seoul}")
    public void transitionDaily() {
        transitionDueMoveIns("일일 스케줄");
    }

    private void transitionDueMoveIns(String trigger) {
        try {
            int updatedRows = preparationService.transitionDueMoveIns();
            if (updatedRows > 0) {
                log.info("{} 입주 전환 완료: {}명", trigger, updatedRows);
            }
        } catch (RuntimeException exception) {
            log.error("{} 입주 전환에 실패했습니다.", trigger, exception);
        }
    }
}

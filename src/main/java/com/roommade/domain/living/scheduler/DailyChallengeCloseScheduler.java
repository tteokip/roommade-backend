package com.roommade.domain.living.scheduler;

import com.roommade.domain.living.service.DailyChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyChallengeCloseScheduler implements SmartInitializingSingleton {

    private final DailyChallengeService dailyChallengeService;

    /** 서버 시작 시 중단 기간에 누락된 챌린지 마감을 보정한다. */
    @Override
    public void afterSingletonsInstantiated() {
        closeDueChallenges("서버 시작");
    }

    /** 한국 시간 기준 매일 00시 05분에 전날 챌린지를 마감하고 코인을 지급한다. */
    @Scheduled(
            cron = "${daily-challenge.close-cron:0 5 0 * * *}",
            zone = "${daily-challenge.close-zone:Asia/Seoul}")
    public void closeDaily() {
        closeDueChallenges("일일 스케줄");
    }

    private void closeDueChallenges(String trigger) {
        try {
            int closedCount = dailyChallengeService.closeDueChallenges();
            if (closedCount > 0) {
                log.info("{} 챌린지 마감 완료: {}건", trigger, closedCount);
            }
        } catch (RuntimeException exception) {
            log.error("{} 챌린지 마감에 실패했습니다.", trigger, exception);
        }
    }
}

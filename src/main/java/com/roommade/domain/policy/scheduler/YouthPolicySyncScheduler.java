package com.roommade.domain.policy.scheduler;

import com.roommade.domain.policy.client.YouthPolicyApiClient;
import com.roommade.domain.policy.service.YouthPolicySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class YouthPolicySyncScheduler {
    private final YouthPolicyApiClient youthPolicyApiClient;
    private final YouthPolicySyncService youthPolicySyncService;

    @Scheduled(cron = "${youth-policy.api.sync-cron:0 10 3 * * *}")
    public void syncYouthPolicies() {
        if (!youthPolicyApiClient.isConfigured()) {
            log.warn("온통청년 API 인증키가 설정되지 않아 청년 정책 자동 동기화를 건너뜁니다.");
            return;
        }
        try {
            youthPolicySyncService.syncYouthPolicies();
        } catch (RuntimeException exception) {
            log.error("청년 정책 자동 동기화에 실패했습니다.", exception);
        }
    }
}

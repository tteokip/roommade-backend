package com.roommade.domain.financialproduct.scheduler;

import com.roommade.domain.financialproduct.service.FinancialProductService;
import com.roommade.domain.financialproduct.client.FssProductApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialProductSyncScheduler {

    private final FinancialProductService financialProductService;
    private final FssProductApiClient fssProductApiClient;

    @Scheduled(cron = "${fss.api.sync-cron:0 0 3 * * *}")
    public void syncProducts() {
        if (!fssProductApiClient.isConfigured()) {
            log.warn("FSS API 인증키가 설정되지 않아 금융상품 자동 동기화를 건너뜁니다.");
            return;
        }
        try {
            financialProductService.syncAllProducts();
            log.info("금융상품 자동 동기화를 완료했습니다.");
        } catch (RuntimeException exception) {
            log.error("금융상품 자동 동기화에 실패했습니다.", exception);
        }
    }
}

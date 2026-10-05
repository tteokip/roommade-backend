package com.roommade.domain.living.controller;

import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.code.LivingSuccessCode;
import com.roommade.domain.living.dto.response.DailyLivingCostOverviewResponse;
import com.roommade.domain.living.dto.response.MonthlyLivingCostResponse;
import com.roommade.domain.living.service.LivingCostService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증이 아직 없어, 사용자 식별을 필수 헤더 {@code X-User-Id}로 임시 대체한다(house 도메인과 동일한 방식).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/living")
public class LivingCostController {

    private final LivingCostService livingCostService;

    @GetMapping("/daily-living-costs")
    public ApiResponse<DailyLivingCostOverviewResponse> getDailyLivingCosts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        DailyLivingCostOverviewResponse response = livingCostService.getDailyLivingCostOverview(userId);
        return ApiResponse.success(LivingSuccessCode.DAILY_LIVING_COST_FOUND, response);
    }

    @GetMapping("/monthly-living-costs")
    public ApiResponse<MonthlyLivingCostResponse> getMonthlyLivingCosts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        MonthlyLivingCostResponse response = livingCostService.getMonthlyLivingCost(userId);
        return ApiResponse.success(LivingSuccessCode.MONTHLY_LIVING_COST_FOUND, response);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(LivingErrorCode.USER_ID_REQUIRED);
        }
    }
}

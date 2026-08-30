package com.roommade.domain.living.controller;

import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.code.LivingSuccessCode;
import com.roommade.domain.living.dto.request.EmergencyFundCurrentAmountRequest;
import com.roommade.domain.living.dto.request.EmergencyFundTargetRequest;
import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import com.roommade.domain.living.service.EmergencyFundService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증이 아직 없어, 사용자 식별을 필수 헤더 {@code X-User-Id}로 임시 대체한다(house 도메인과 동일한 방식).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/living/emergency-funds")
public class EmergencyFundController {

    private final EmergencyFundService emergencyFundService;

    @GetMapping
    public ApiResponse<EmergencyFundResponse> getEmergencyFund(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        EmergencyFundResponse response = emergencyFundService.getEmergencyFund(userId);
        return ApiResponse.success(LivingSuccessCode.EMERGENCY_FUND_FOUND, response);
    }

    @PutMapping("/target")
    public ApiResponse<EmergencyFundResponse> setTarget(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody EmergencyFundTargetRequest request) {
        validateUserId(userId);
        EmergencyFundResponse response = emergencyFundService.setTarget(userId, request.getTargetAmount());
        return ApiResponse.success(LivingSuccessCode.EMERGENCY_FUND_TARGET_UPDATED, response);
    }

    @PutMapping("/current-amount")
    public ApiResponse<EmergencyFundResponse> updateCurrentAmount(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody EmergencyFundCurrentAmountRequest request) {
        validateUserId(userId);
        EmergencyFundResponse response =
                emergencyFundService.updateCurrentAmount(userId, request.getCurrentAmount());
        return ApiResponse.success(LivingSuccessCode.EMERGENCY_FUND_CURRENT_AMOUNT_UPDATED, response);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(LivingErrorCode.USER_ID_REQUIRED);
        }
    }
}
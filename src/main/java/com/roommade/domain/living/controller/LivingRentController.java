package com.roommade.domain.living.controller;

import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.code.LivingSuccessCode;
import com.roommade.domain.living.dto.request.MonthlyRentRequest;
import com.roommade.domain.living.dto.response.LivingRentResponse;
import com.roommade.domain.living.service.LivingRentService;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
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
@RequestMapping("/api/living")
public class LivingRentController {

    private final LivingRentService livingRentService;

    @PutMapping("/rent")
    public ApiResponse<LivingRentResponse> setMonthlyRent(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody MonthlyRentRequest request) {
        validateUserId(userId);
        LivingRentResponse response = livingRentService.setMonthlyRent(userId, request.getMonthlyRent());
        return ApiResponse.success(LivingSuccessCode.LIVING_RENT_UPDATED, response);
    }

    @GetMapping("/rir")
    public ApiResponse<RirDiagnosisResponse> getRirDiagnosis(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        RirDiagnosisResponse response = livingRentService.getRirDiagnosis(userId);
        return ApiResponse.success(LivingSuccessCode.RIR_DIAGNOSIS_FOUND, response);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(LivingErrorCode.USER_ID_REQUIRED);
        }
    }
}

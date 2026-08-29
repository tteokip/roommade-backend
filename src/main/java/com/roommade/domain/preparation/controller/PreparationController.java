package com.roommade.domain.preparation.controller;

import com.roommade.domain.preparation.code.PreparationSuccessCode;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/preparations")
public class PreparationController {

    private final PreparationService preparationService;

    /** 사용자 RIR 진단 결과 조회. */
    @GetMapping("/rir")
    public ApiResponse<RirDiagnosisResponse> getRirDiagnosis(
            @RequestHeader("X-User-Id") Long userId) {
        RirDiagnosisResponse response = preparationService.getRirDiagnosis(userId);
        return ApiResponse.success(PreparationSuccessCode.RIR_DIAGNOSIS_FOUND, response);
    }
}

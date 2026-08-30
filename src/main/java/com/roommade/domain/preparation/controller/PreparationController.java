package com.roommade.domain.preparation.controller;

import com.roommade.domain.preparation.code.PreparationSuccessCode;
import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.HouseComparisonProgressResponse;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
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

    /** 사용자 보증금 마련 현황 조회. */
    @GetMapping("/deposit")
    public ApiResponse<DepositProgressResponse> getDepositProgress(
            @RequestHeader("X-User-Id") Long userId) {
        DepositProgressResponse response = preparationService.getDepositProgress(userId);
        return ApiResponse.success(PreparationSuccessCode.DEPOSIT_PROGRESS_FOUND, response);
    }

    /** 사용자 집 비교 점수 조회. */
    @GetMapping("/house-comparison")
    public ApiResponse<HouseComparisonProgressResponse> getHouseComparisonProgress(
            @RequestHeader("X-User-Id") Long userId) {
        HouseComparisonProgressResponse response =
                preparationService.getHouseComparisonProgress(userId);
        return ApiResponse.success(
                PreparationSuccessCode.HOUSE_COMPARISON_PROGRESS_FOUND,
                response);
    }

    /** 사용자 자립 준비도 전체 진단 결과 조회. */
    @GetMapping("/readiness")
    public ApiResponse<ReadinessDiagnosisResponse> getReadinessDiagnosis(
            @RequestHeader("X-User-Id") Long userId) {
        ReadinessDiagnosisResponse response =
                preparationService.getReadinessDiagnosis(userId);
        return ApiResponse.success(
                PreparationSuccessCode.READINESS_DIAGNOSIS_FOUND,
                response);
    }
}

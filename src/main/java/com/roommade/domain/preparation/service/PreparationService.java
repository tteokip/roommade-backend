package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;

public interface PreparationService {

    /** 사용자 월 소득과 예상 월세 기반 RIR 진단 결과 조회. */
    RirDiagnosisResponse getRirDiagnosis(Long userId);

    /** 사용자 목표 보증금과 현재 마련 금액 기반 보증금 현황 조회. */
    DepositProgressResponse getDepositProgress(Long userId);
}

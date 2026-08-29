package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;

public interface PreparationService {

    /** 사용자 월 소득과 예상 월세 기반 RIR 진단 결과 조회. */
    RirDiagnosisResponse getRirDiagnosis(Long userId);
}

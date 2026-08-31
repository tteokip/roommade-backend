package com.roommade.domain.living.service;

import com.roommade.domain.living.dto.response.LivingRentResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;

public interface LivingRentService {

    LivingRentResponse setMonthlyRent(Long userId, Long monthlyRent);

    RirDiagnosisResponse getRirDiagnosis(Long userId);
}

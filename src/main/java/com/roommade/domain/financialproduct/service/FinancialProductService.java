package com.roommade.domain.financialproduct.service;

import com.roommade.domain.financialproduct.dto.response.FinancialProductDetailResponse;
import com.roommade.domain.financialproduct.dto.response.FinancialProductSummaryResponse;
import java.util.List;

public interface FinancialProductService {
    void syncAllProducts();
    List<FinancialProductSummaryResponse> getDepositProducts();
    FinancialProductDetailResponse getDepositProduct(Long productId);
    List<FinancialProductSummaryResponse> getSavingProducts();
    FinancialProductDetailResponse getSavingProduct(Long productId);
}

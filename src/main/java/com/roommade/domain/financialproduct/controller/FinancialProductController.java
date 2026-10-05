package com.roommade.domain.financialproduct.controller;

import com.roommade.domain.financialproduct.code.FinancialProductSuccessCode;
import com.roommade.domain.financialproduct.dto.response.FinancialProductDetailResponse;
import com.roommade.domain.financialproduct.dto.response.FinancialProductSummaryResponse;
import com.roommade.domain.financialproduct.service.FinancialProductService;
import com.roommade.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/financial-products")
@RequiredArgsConstructor
public class FinancialProductController {
    private final FinancialProductService financialProductService;
    @GetMapping("/deposits") public ResponseEntity<ApiResponse<List<FinancialProductSummaryResponse>>> deposits() { return ResponseEntity.ok(ApiResponse.success(FinancialProductSuccessCode.PRODUCTS_RETRIEVED, financialProductService.getDepositProducts())); }
    @GetMapping("/deposits/{productId}") public ResponseEntity<ApiResponse<FinancialProductDetailResponse>> deposit(@PathVariable Long productId) { return ResponseEntity.ok(ApiResponse.success(FinancialProductSuccessCode.PRODUCT_RETRIEVED, financialProductService.getDepositProduct(productId))); }
    @GetMapping("/savings") public ResponseEntity<ApiResponse<List<FinancialProductSummaryResponse>>> savings() { return ResponseEntity.ok(ApiResponse.success(FinancialProductSuccessCode.PRODUCTS_RETRIEVED, financialProductService.getSavingProducts())); }
    @GetMapping("/savings/{productId}") public ResponseEntity<ApiResponse<FinancialProductDetailResponse>> saving(@PathVariable Long productId) { return ResponseEntity.ok(ApiResponse.success(FinancialProductSuccessCode.PRODUCT_RETRIEVED, financialProductService.getSavingProduct(productId))); }
    @PostMapping("/sync") public ResponseEntity<ApiResponse<Void>> sync() { financialProductService.syncAllProducts(); return ResponseEntity.ok(ApiResponse.success(FinancialProductSuccessCode.PRODUCTS_SYNCED)); }
}

package com.roommade.domain.financialproduct.code;

import com.roommade.global.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FinancialProductSuccessCode implements SuccessCode {
    PRODUCTS_RETRIEVED(HttpStatus.OK, "FINANCIAL_PRODUCT_000", "금융상품을 조회했습니다."),
    PRODUCT_RETRIEVED(HttpStatus.OK, "FINANCIAL_PRODUCT_001", "금융상품 상세 정보를 조회했습니다."),
    PRODUCTS_SYNCED(HttpStatus.OK, "FINANCIAL_PRODUCT_002", "금융상품을 동기화했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

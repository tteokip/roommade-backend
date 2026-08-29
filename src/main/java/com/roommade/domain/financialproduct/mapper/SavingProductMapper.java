package com.roommade.domain.financialproduct.mapper;

import com.roommade.domain.financialproduct.domain.FinancialProduct;
import com.roommade.domain.financialproduct.domain.FinancialProductOption;
import com.roommade.domain.financialproduct.dto.response.FinancialProductDetailResponse;
import com.roommade.domain.financialproduct.dto.response.FinancialProductOptionResponse;
import com.roommade.domain.financialproduct.dto.response.FinancialProductSummaryResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SavingProductMapper {
    int upsert(FinancialProduct product);
    int upsertOption(FinancialProductOption option);
    List<FinancialProductSummaryResponse> findProducts();
    FinancialProductDetailResponse findProductById(@Param("productId") Long productId);
    List<FinancialProductOptionResponse> findOptionsByProductId(@Param("productId") Long productId);
}

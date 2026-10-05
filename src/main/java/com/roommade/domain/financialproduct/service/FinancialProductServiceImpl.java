package com.roommade.domain.financialproduct.service;

import com.roommade.domain.financialproduct.client.FssProductApiClient;
import com.roommade.domain.financialproduct.code.FinancialProductErrorCode;
import com.roommade.domain.financialproduct.domain.FinancialInstitution;
import com.roommade.domain.financialproduct.domain.FinancialProduct;
import com.roommade.domain.financialproduct.domain.FinancialProductOption;
import com.roommade.domain.financialproduct.dto.external.FssProduct;
import com.roommade.domain.financialproduct.dto.external.FssProductApiResponse;
import com.roommade.domain.financialproduct.dto.external.FssProductOption;
import com.roommade.domain.financialproduct.dto.response.FinancialProductDetailResponse;
import com.roommade.domain.financialproduct.dto.response.FinancialProductSummaryResponse;
import com.roommade.domain.financialproduct.mapper.DepositProductMapper;
import com.roommade.domain.financialproduct.mapper.FinancialInstitutionMapper;
import com.roommade.domain.financialproduct.mapper.SavingProductMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinancialProductServiceImpl implements FinancialProductService {
    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private final FssProductApiClient apiClient;
    private final FinancialInstitutionMapper institutionMapper;
    private final DepositProductMapper depositProductMapper;
    private final SavingProductMapper savingProductMapper;

    @Override @Transactional
    public void syncAllProducts() {
        sync(apiClient.getDepositProducts(), false);
        sync(apiClient.getSavingProducts(), true);
    }

    private void sync(FssProductApiResponse response, boolean saving) {
        if (response == null || response.getResult() == null || !"000".equals(response.getResult().getErrorCode())) {
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
        Map<String, List<FssProductOption>> options = new HashMap<>();
        for (FssProductOption option : response.getResult().getOptionList()) {
            if (!isTargetFinancialCompany(option.getFinancialCompanyCode())) continue;
            options.computeIfAbsent(option.getFinancialProductCode(), ignored -> new java.util.ArrayList<>()).add(option);
        }
        for (FssProduct source : response.getResult().getBaseList()) {
            if (!isTargetFinancialCompany(source.getFinancialCompanyCode()) || isBlank(source.getFinancialProductCode())) continue;
            FinancialInstitution institution = FinancialInstitution.builder()
                    .financialInstitutionCode(source.getFinancialCompanyCode())
                    .financialInstitutionName(source.getFinancialCompanyName()).build();
            institutionMapper.upsert(institution);
            FinancialProduct product = toProduct(source, institution.getFinancialInstitutionId());
            if (saving) savingProductMapper.upsert(product); else depositProductMapper.upsert(product);
            for (FssProductOption option : options.getOrDefault(source.getFinancialProductCode(), List.of())) {
                if (isBlank(option.getSaveTerm()) || isBlank(option.getInterestRateType())) continue;
                try {
                    FinancialProductOption command = FinancialProductOption.builder().productId(product.getProductId())
                            .interestRateType(option.getInterestRateType()).reserveType(option.getReserveType())
                            .saveTerm(Integer.valueOf(option.getSaveTerm())).baseInterestRate(option.getInterestRate())
                            .maxInterestRate(option.getMaximumInterestRate()).build();
                    if (saving && !isBlank(option.getReserveType())) savingProductMapper.upsertOption(command);
                    else if (!saving) depositProductMapper.upsertOption(command);
                } catch (NumberFormatException ignored) { }
            }
        }
    }

    private FinancialProduct toProduct(FssProduct source, Long institutionId) {
        return FinancialProduct.builder().financialInstitutionId(institutionId)
                .productCode(source.getFinancialProductCode()).productName(source.getFinancialProductName())
                .joinMethod(source.getJoinWay()).joinTarget(source.getJoinMember()).joinRestriction(source.getJoinDeny())
                .specialCondition(source.getSpecialCondition()).maturityInterest(source.getMaturityInterest())
                .maxLimit(source.getMaxLimit()).notice(source.getEtcNote()).disclosureMonth(source.getDisclosureMonth())
                .disclosureStartDate(parseDate(source.getDisclosureStartDay())).disclosureEndDate(parseDate(source.getDisclosureEndDay()))
                .submittedAt(parseDateTime(source.getFinancialCompanySubmittedDay())).build();
    }
    private LocalDate parseDate(String value) { try { return isBlank(value) ? null : LocalDate.parse(value, DATE); } catch (RuntimeException e) { return null; } }
    private LocalDateTime parseDateTime(String value) { try { return isBlank(value) ? null : LocalDateTime.parse(value, DATE_TIME); } catch (RuntimeException e) { return null; } }
    private boolean isBlank(String value) { return value == null || value.isBlank(); }
    private boolean isTargetFinancialCompany(String financialCompanyCode) {
        return apiClient.getFinancialCompanyCode().equals(financialCompanyCode);
    }

    @Override @Transactional(readOnly = true) public List<FinancialProductSummaryResponse> getDepositProducts() { return depositProductMapper.findProducts(); }
    @Override @Transactional(readOnly = true) public FinancialProductDetailResponse getDepositProduct(Long id) {
        FinancialProductDetailResponse product = find(depositProductMapper.findProductById(id));
        product.setOptions(depositProductMapper.findOptionsByProductId(id));
        return product;
    }
    @Override @Transactional(readOnly = true) public List<FinancialProductSummaryResponse> getSavingProducts() { return savingProductMapper.findProducts(); }
    @Override @Transactional(readOnly = true) public FinancialProductDetailResponse getSavingProduct(Long id) {
        FinancialProductDetailResponse product = find(savingProductMapper.findProductById(id));
        product.setOptions(savingProductMapper.findOptionsByProductId(id));
        return product;
    }
    private FinancialProductDetailResponse find(FinancialProductDetailResponse result) { if (result == null) throw new BusinessException(FinancialProductErrorCode.PRODUCT_NOT_FOUND); return result; }
}

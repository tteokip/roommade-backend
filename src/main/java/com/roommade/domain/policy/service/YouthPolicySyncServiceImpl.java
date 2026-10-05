package com.roommade.domain.policy.service;

import com.roommade.domain.policy.client.YouthPolicyApiClient;
import com.roommade.domain.policy.domain.YouthPolicy;
import com.roommade.domain.policy.domain.YouthPolicyRegion;
import com.roommade.domain.policy.dto.external.YouthPolicyApiItem;
import com.roommade.domain.policy.dto.external.YouthPolicyApiPaging;
import com.roommade.domain.policy.dto.external.YouthPolicyApiResponse;
import com.roommade.domain.policy.mapper.YouthPolicyMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YouthPolicySyncServiceImpl implements YouthPolicySyncService {
    private static final DateTimeFormatter APPLICATION_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final Pattern APPLICATION_PERIOD = Pattern.compile("^(\\d{8})\\s*~\\s*(\\d{8})$");
    private static final List<String> TARGET_POLICY_KEYWORDS = List.of(
            "주거지원", "공공임대주택", "주택");
    private final YouthPolicyApiClient apiClient;
    private final YouthPolicyMapper youthPolicyMapper;

    @Override
    @Transactional
    public int syncYouthPolicies() {
        int syncedCount = 0;
        for (String keyword : TARGET_POLICY_KEYWORDS) {
            YouthPolicyApiResponse firstResponse = apiClient.getYouthPolicies(1, keyword);
            YouthPolicyApiPaging paging = validate(firstResponse);
            syncedCount += syncItems(firstResponse);
            int totalPages = (int) Math.ceil((double) paging.getTotCount() / paging.getPageSize());
            for (int pageNum = 2; pageNum <= totalPages; pageNum++) {
                YouthPolicyApiResponse response = apiClient.getYouthPolicies(pageNum, keyword);
                validate(response);
                syncedCount += syncItems(response);
            }
        }
        log.info("청년 정책 동기화를 완료했습니다. 저장 건수={}", syncedCount);
        return syncedCount;
    }

    private YouthPolicyApiPaging validate(YouthPolicyApiResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getPagging() == null
                || response.getResult().getYouthPolicyList() == null) {
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
        YouthPolicyApiPaging paging = response.getResult().getPagging();
        if (paging.getTotCount() == null || paging.getTotCount() < 0
                || paging.getPageSize() == null || paging.getPageSize() <= 0) {
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
        return paging;
    }

    private int syncItems(YouthPolicyApiResponse response) {
        int count = 0;
        for (YouthPolicyApiItem source : response.getResult().getYouthPolicyList()) {
            if (source == null || isBlank(source.getPlcyNo()) || isBlank(source.getPlcyNm())) {
                continue;
            }
            youthPolicyMapper.upsert(toYouthPolicy(source));
            Long youthPolicyId = youthPolicyMapper.findYouthPolicyIdByPolicyNo(source.getPlcyNo());
            if (youthPolicyId == null) {
                throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
            }
            youthPolicyMapper.deleteRegionsByYouthPolicyId(youthPolicyId);
            List<YouthPolicyRegion> regions = YouthPolicyRegionResolver.resolve(source.getZipCd());
            if (!regions.isEmpty()) {
                youthPolicyMapper.insertRegions(youthPolicyId, regions);
            }
            count++;
        }
        return count;
    }

    private YouthPolicy toYouthPolicy(YouthPolicyApiItem source) {
        LocalDate[] applicationPeriod = parseApplicationPeriod(source.getAplyYmd());
        return YouthPolicy.builder()
                .policyNo(source.getPlcyNo()).policyName(source.getPlcyNm())
                .policyKeyword(source.getPlcyKywdNm()).policyDescription(source.getPlcyExplnCn())
                .supportContent(source.getPlcySprtCn()).providerInstitutionCode(source.getSprvsnInstCd())
                .providerInstitutionName(source.getSprvsnInstCdNm()).zipCd(source.getZipCd())
                .applicationStartDate(applicationPeriod[0]).applicationEndDate(applicationPeriod[1])
                .applicationPeriodText(source.getAplyYmd()).applicationMethod(source.getPlcyAplyMthdCn())
                .applicationUrl(source.getAplyUrlAddr()).referenceUrl(source.getRefUrlAddr1())
                .minAge(toInteger(source.getSprtTrgtMinAge())).maxAge(toInteger(source.getSprtTrgtMaxAge()))
                .incomeConditionCode(source.getEarnCndSeCd()).minIncome(toLong(source.getEarnMinAmt()))
                .maxIncome(toPositiveLong(source.getEarnMaxAmt())).incomeConditionText(source.getEarnEtcCn())
                .qualification(joinQualification(source.getAddAplyQlfcCndCn(), source.getPtcpPrpTrgtCn()))
                .syncedAt(LocalDateTime.now()).build();
    }

    private LocalDate[] parseApplicationPeriod(String value) {
        if (isBlank(value)) return new LocalDate[] {null, null};
        Matcher matcher = APPLICATION_PERIOD.matcher(value.trim());
        if (!matcher.matches()) return new LocalDate[] {null, null};
        try {
            return new LocalDate[] {LocalDate.parse(matcher.group(1), APPLICATION_DATE), LocalDate.parse(matcher.group(2), APPLICATION_DATE)};
        } catch (RuntimeException exception) {
            return new LocalDate[] {null, null};
        }
    }

    private Integer toInteger(String value) { try { return isBlank(value) ? null : Integer.valueOf(value.trim()); } catch (NumberFormatException e) { return null; } }
    private Long toLong(String value) { try { return isBlank(value) ? null : Long.valueOf(value.trim()); } catch (NumberFormatException e) { return null; } }
    private Long toPositiveLong(String value) { Long parsed = toLong(value); return parsed == null || parsed == 0 ? null : parsed; }
    private String joinQualification(String additional, String participant) {
        if (isBlank(additional)) return isBlank(participant) ? null : participant;
        return isBlank(participant) ? additional : additional + "\n" + participant;
    }
    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}

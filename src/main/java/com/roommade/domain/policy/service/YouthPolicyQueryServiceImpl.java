package com.roommade.domain.policy.service;

import com.roommade.domain.policy.code.YouthPolicyErrorCode;
import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyListResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyPageResponse;
import com.roommade.domain.policy.mapper.YouthPolicyMapper;
import com.roommade.domain.user.dto.response.UserPolicyProfileResponse;
import com.roommade.domain.user.mapper.UserProfileMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YouthPolicyQueryServiceImpl implements YouthPolicyQueryService {
    private static final int MAX_PAGE_SIZE = 100;
    private final YouthPolicyMapper youthPolicyMapper;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public YouthPolicyPageResponse getYouthPolicies(Long userId, String region, int page, int size) {
        validate(page, size);
        UserPolicyProfileResponse profile = userProfileMapper.findPolicyProfileByUserId(userId);
        if (profile == null || profile.getBirthDate() == null || profile.getMonthlyIncome() == null) {
            throw new BusinessException(YouthPolicyErrorCode.USER_PROFILE_NOT_FOUND);
        }

        int age = calculateAge(profile.getBirthDate());
        long annualIncomeInTenThousandWon = calculateAnnualIncomeInTenThousandWon(profile.getMonthlyIncome());
        String regionCode = resolveRegionCode(region);
        long totalElements = youthPolicyMapper.countYouthPolicies(regionCode, age, annualIncomeInTenThousandWon);
        List<YouthPolicyListResponse> content = youthPolicyMapper.findYouthPolicies(
                regionCode, age, annualIncomeInTenThousandWon, (long) (page - 1) * size, size);
        return new YouthPolicyPageResponse(profile.getName(), content, page, size, totalElements,
                (int) Math.ceil((double) totalElements / size));
    }

    @Override
    @Transactional(readOnly = true)
    public YouthPolicyDetailResponse getYouthPolicyDetail(Long youthPolicyId) {
        if (youthPolicyId == null || youthPolicyId <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        YouthPolicyDetailResponse policy = youthPolicyMapper.findYouthPolicyById(youthPolicyId);
        if (policy == null) {
            throw new BusinessException(YouthPolicyErrorCode.YOUTH_POLICY_NOT_FOUND);
        }
        return policy;
    }

    private String resolveRegionCode(String region) {
        String regionCode = YouthPolicyRegionResolver.resolveRegionCode(region);
        if (region != null && !region.isBlank() && regionCode == null) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        return regionCode;
    }

    private void validate(int page, int size) {
        if (page < 1 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private long calculateAnnualIncomeInTenThousandWon(long monthlyIncome) {
        if (monthlyIncome < 0) {
            throw new BusinessException(YouthPolicyErrorCode.USER_PROFILE_NOT_FOUND);
        }
        return monthlyIncome * 12 / 10_000;
    }
}

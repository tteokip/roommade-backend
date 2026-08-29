package com.roommade.domain.policy.service;

import com.roommade.domain.policy.code.YouthPolicyErrorCode;
import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyListResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyPageResponse;
import com.roommade.domain.policy.mapper.YouthPolicyMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YouthPolicyQueryServiceImpl implements YouthPolicyQueryService {
    private static final int MAX_PAGE_SIZE = 100;
    private final YouthPolicyMapper youthPolicyMapper;

    @Override
    @Transactional(readOnly = true)
    public YouthPolicyPageResponse getYouthPolicies(String region, Integer age, Long income, int page, int size) {
        validate(page, size, age, income);
        String regionCode = resolveRegionCode(region);
        long totalElements = youthPolicyMapper.countYouthPolicies(regionCode, age, income);
        List<YouthPolicyListResponse> content = youthPolicyMapper.findYouthPolicies(
                regionCode, age, income, (long) (page - 1) * size, size);
        return new YouthPolicyPageResponse(content, page, size, totalElements,
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

    private void validate(int page, int size, Integer age, Long income) {
        if (page < 1 || size < 1 || size > MAX_PAGE_SIZE
                || (age != null && age < 0) || (income != null && income < 0)) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}

package com.roommade.domain.policy.service;

import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyPageResponse;

public interface YouthPolicyQueryService {
    YouthPolicyPageResponse getYouthPolicies(Long userId, String region, int page, int size);
    YouthPolicyDetailResponse getYouthPolicyDetail(Long youthPolicyId);
}

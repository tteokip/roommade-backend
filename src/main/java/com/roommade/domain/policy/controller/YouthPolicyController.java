package com.roommade.domain.policy.controller;

import com.roommade.domain.policy.code.YouthPolicySuccessCode;
import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyPageResponse;
import com.roommade.domain.policy.service.YouthPolicyQueryService;
import com.roommade.domain.policy.code.YouthPolicyErrorCode;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/youth-policies")
@RequiredArgsConstructor
public class YouthPolicyController {
    private final YouthPolicyQueryService youthPolicyQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<YouthPolicyPageResponse>> getYouthPolicies(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        validateUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(
                YouthPolicySuccessCode.YOUTH_POLICIES_RETRIEVED,
                youthPolicyQueryService.getYouthPolicies(userId, region, page, size)));
    }

    @GetMapping("/{youthPolicyId}")
    public ResponseEntity<ApiResponse<YouthPolicyDetailResponse>> getYouthPolicyDetail(
            @PathVariable Long youthPolicyId) {
        return ResponseEntity.ok(ApiResponse.success(
                YouthPolicySuccessCode.YOUTH_POLICY_RETRIEVED,
                youthPolicyQueryService.getYouthPolicyDetail(youthPolicyId)));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(YouthPolicyErrorCode.USER_ID_REQUIRED);
        }
    }
}

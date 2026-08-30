package com.roommade.domain.policy.controller;

import com.roommade.domain.policy.code.YouthPolicySuccessCode;
import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyPageResponse;
import com.roommade.domain.policy.service.YouthPolicyQueryService;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/youth-policies")
@RequiredArgsConstructor
public class YouthPolicyController {
    private final YouthPolicyQueryService youthPolicyQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<YouthPolicyPageResponse>> getYouthPolicies(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Long income,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                YouthPolicySuccessCode.YOUTH_POLICIES_RETRIEVED,
                youthPolicyQueryService.getYouthPolicies(region, age, income, page, size)));
    }

    @GetMapping("/{youthPolicyId}")
    public ResponseEntity<ApiResponse<YouthPolicyDetailResponse>> getYouthPolicyDetail(
            @PathVariable Long youthPolicyId) {
        return ResponseEntity.ok(ApiResponse.success(
                YouthPolicySuccessCode.YOUTH_POLICY_RETRIEVED,
                youthPolicyQueryService.getYouthPolicyDetail(youthPolicyId)));
    }
}

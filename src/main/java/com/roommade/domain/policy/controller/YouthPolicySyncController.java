package com.roommade.domain.policy.controller;

import com.roommade.domain.policy.code.YouthPolicySuccessCode;
import com.roommade.domain.policy.dto.response.YouthPolicySyncResponse;
import com.roommade.domain.policy.service.YouthPolicySyncService;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/youth-policies")
@RequiredArgsConstructor
public class YouthPolicySyncController {
    private final YouthPolicySyncService youthPolicySyncService;

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<YouthPolicySyncResponse>> syncYouthPolicies() {
        int syncedCount = youthPolicySyncService.syncYouthPolicies();
        return ResponseEntity.ok(ApiResponse.success(
                YouthPolicySuccessCode.YOUTH_POLICIES_SYNCED,
                new YouthPolicySyncResponse(syncedCount)));
    }
}

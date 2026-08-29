package com.roommade.domain.policy.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YouthPolicyPageResponse {
    private final List<YouthPolicyListResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}

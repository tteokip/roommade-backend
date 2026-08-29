package com.roommade.domain.policy.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YouthPolicyListResponse {
    private Long youthPolicyId;
    private String policyName;
    private String policyKeyword;
    private String providerInstitutionName;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String applicationPeriod;
    private Long dDay;
}

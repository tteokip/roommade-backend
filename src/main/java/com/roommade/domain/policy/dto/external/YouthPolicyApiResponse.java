package com.roommade.domain.policy.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouthPolicyApiResponse {
    private Integer resultCode;
    private String resultMessage;
    private YouthPolicyApiResult result;
}

package com.roommade.domain.policy.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouthPolicyApiResult {
    private YouthPolicyApiPaging pagging;
    private List<YouthPolicyApiItem> youthPolicyList;
}

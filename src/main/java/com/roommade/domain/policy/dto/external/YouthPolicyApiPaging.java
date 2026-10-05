package com.roommade.domain.policy.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouthPolicyApiPaging {
    private Integer totCount;
    private Integer pageNum;
    private Integer pageSize;
}

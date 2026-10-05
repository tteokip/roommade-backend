package com.roommade.domain.preparation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RirProfileResponse {

    /** 원 단위 월 소득. */
    private Long monthlyIncome;

    /** 원 단위 월세 상한. */
    private Long monthlyRentLimit;
}

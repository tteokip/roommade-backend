package com.roommade.domain.preparation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RirProfileResponse {

    private Long monthlyIncome;
    private Long monthlyRentLimit;
}

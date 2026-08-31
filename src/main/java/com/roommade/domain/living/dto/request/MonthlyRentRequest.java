package com.roommade.domain.living.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRentRequest {

    @NotNull
    @Positive
    private Long monthlyRent;
}

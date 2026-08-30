package com.roommade.domain.living.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyFundCurrentAmountRequest {

    @NotNull
    @PositiveOrZero
    private Long currentAmount;
}

package com.roommade.domain.living.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyLivingCostItemResponse {

    private LocalDate spendingDate;
    private Long totalAmount;
}

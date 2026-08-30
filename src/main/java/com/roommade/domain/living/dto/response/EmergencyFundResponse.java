package com.roommade.domain.living.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyFundResponse {

    private Long targetAmount;
    private Long currentAmount;
    private boolean achieved;
    private LocalDateTime achievedAt;

    public static EmergencyFundResponse notStarted() {
        return new EmergencyFundResponse(0L, 0L, false, null);
    }
}
package com.roommade.domain.house.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseConfirmRequest {

    public enum ConfirmationType {
        COMPARISON,
        OTHER
    }

    @NotNull
    private ConfirmationType confirmationType;

    private Long houseId;
}

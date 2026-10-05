package com.roommade.domain.room.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureRewardClaimRequest {

    @NotNull
    @Positive
    private Long furnitureId;
}

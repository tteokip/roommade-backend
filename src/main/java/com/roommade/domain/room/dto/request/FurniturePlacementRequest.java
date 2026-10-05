package com.roommade.domain.room.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FurniturePlacementRequest {

    @NotNull
    private Boolean placed;
}

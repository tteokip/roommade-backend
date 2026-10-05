package com.roommade.domain.room.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {

    private BigDecimal readinessScore;
    private List<RoomFurnitureResponse> furniture;
}

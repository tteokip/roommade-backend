package com.roommade.domain.room.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopFurnitureListResponse {

    private List<ShopFurnitureResponse> furniture;
}

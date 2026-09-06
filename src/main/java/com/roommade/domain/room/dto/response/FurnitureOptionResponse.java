package com.roommade.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureOptionResponse {

    private Long furnitureId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String assetUrl;
}

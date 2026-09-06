package com.roommade.domain.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomFurnitureResponse {

    private Long furnitureId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String assetUrl;
    private boolean placed;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime acquiredAt;
}

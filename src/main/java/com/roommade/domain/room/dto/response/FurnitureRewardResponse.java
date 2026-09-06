package com.roommade.domain.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FurnitureRewardResponse {

    private Long rewardId;
    private Integer rewardStage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime grantedAt;

    private List<FurnitureOptionResponse> choices;
}

package com.roommade.domain.room.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureRewardSourceResponse {

    private Long rewardId;
    private Integer rewardStage;
    private LocalDateTime grantedAt;
}

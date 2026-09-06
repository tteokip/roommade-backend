package com.roommade.domain.room.service;

import com.roommade.domain.room.dto.response.FurnitureRewardsResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.RoomResponse;

public interface RoomService {

    RoomResponse getRoom(Long userId);

    FurnitureRewardsResponse getPendingRewards(Long userId);

    RoomFurnitureResponse claimReward(Long userId, Long rewardId, Long furnitureId);

    RoomFurnitureResponse updatePlacement(Long userId, Long furnitureId, boolean placed);

    void grantAllBasicFurniture(Long userId);
}

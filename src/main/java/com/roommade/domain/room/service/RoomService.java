package com.roommade.domain.room.service;

import com.roommade.domain.room.dto.response.FurnitureRewardsResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.RoomResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureListResponse;

public interface RoomService {

    RoomResponse getRoom(Long userId);

    ShopFurnitureListResponse getShopFurniture(Long userId, Long categoryId);

    FurnitureRewardsResponse getPendingRewards(Long userId);

    RoomFurnitureResponse claimReward(Long userId, Long rewardId, Long furnitureId);

    RoomFurnitureResponse updatePlacement(Long userId, Long furnitureId, boolean placed);

    RoomFurnitureResponse purchaseFurniture(Long userId, Long furnitureId);

    void grantAllBasicFurniture(Long userId);
}

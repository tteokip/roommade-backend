package com.roommade.domain.room.mapper;

import com.roommade.domain.room.dto.response.FurnitureOptionResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardSourceResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomMapper {

    int insertInitialFurniture(@Param("userId") Long userId);

    int insertRewardIfAbsent(
            @Param("userId") Long userId,
            @Param("rewardStage") int rewardStage);

    List<RoomFurnitureResponse> findOwnedFurnitureByUserId(@Param("userId") Long userId);

    RoomFurnitureResponse findOwnedFurnitureById(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);

    List<FurnitureRewardSourceResponse> findPendingRewardsByUserId(
            @Param("userId") Long userId);

    FurnitureRewardSourceResponse findPendingRewardForUpdate(
            @Param("userId") Long userId,
            @Param("rewardId") Long rewardId);

    List<FurnitureOptionResponse> findSelectableFurniture(
            @Param("userId") Long userId,
            @Param("rewardStage") int rewardStage);

    boolean existsSelectableFurniture(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId,
            @Param("rewardStage") int rewardStage);

    int insertUserFurniture(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);

    int claimReward(
            @Param("userId") Long userId,
            @Param("rewardId") Long rewardId,
            @Param("furnitureId") Long furnitureId);

    boolean existsOwnedFurniture(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);

    int unplaceFurnitureInSameCategory(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);

    int updateFurniturePlacement(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId,
            @Param("placed") boolean placed);

    int insertAllMissingBasicFurniture(@Param("userId") Long userId);

    List<ShopFurnitureResponse> findShopFurniture(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId);

    Integer findShopFurniturePrice(@Param("furnitureId") Long furnitureId);

    boolean existsUnlockedCategoryForShopFurniture(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);

    int insertPurchasedFurniture(
            @Param("userId") Long userId,
            @Param("furnitureId") Long furnitureId);
}

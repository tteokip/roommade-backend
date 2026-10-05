package com.roommade.domain.room.controller;

import com.roommade.domain.room.code.RoomErrorCode;
import com.roommade.domain.room.code.RoomSuccessCode;
import com.roommade.domain.room.dto.request.FurniturePlacementRequest;
import com.roommade.domain.room.dto.request.FurnitureRewardClaimRequest;
import com.roommade.domain.room.dto.response.FurnitureRewardsResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.RoomResponse;
import com.roommade.domain.room.dto.response.ShopFurnitureListResponse;
import com.roommade.domain.room.service.RoomService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ApiResponse<RoomResponse> getRoom(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        return ApiResponse.success(RoomSuccessCode.ROOM_FOUND, roomService.getRoom(userId));
    }

    @GetMapping("/furniture-rewards")
    public ApiResponse<FurnitureRewardsResponse> getPendingRewards(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        return ApiResponse.success(
                RoomSuccessCode.FURNITURE_REWARDS_FOUND,
                roomService.getPendingRewards(userId));
    }

    @PostMapping("/furniture-rewards/{rewardId}/claim")
    public ApiResponse<RoomFurnitureResponse> claimReward(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long rewardId,
            @Valid @RequestBody FurnitureRewardClaimRequest request) {
        validateUserId(userId);
        return ApiResponse.success(
                RoomSuccessCode.FURNITURE_REWARD_CLAIMED,
                roomService.claimReward(userId, rewardId, request.getFurnitureId()));
    }

    @PatchMapping("/furniture/{furnitureId}/placement")
    public ApiResponse<RoomFurnitureResponse> updatePlacement(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long furnitureId,
            @Valid @RequestBody FurniturePlacementRequest request) {
        validateUserId(userId);
        return ApiResponse.success(
                RoomSuccessCode.FURNITURE_PLACEMENT_UPDATED,
                roomService.updatePlacement(userId, furnitureId, request.getPlaced()));
    }

    @GetMapping("/shop/furniture")
    public ApiResponse<ShopFurnitureListResponse> getShopFurniture(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) Long categoryId) {
        validateUserId(userId);
        return ApiResponse.success(
                RoomSuccessCode.SHOP_FURNITURE_FOUND,
                roomService.getShopFurniture(userId, categoryId));
    }

    @PostMapping("/furniture/{furnitureId}/purchase")
    public ApiResponse<RoomFurnitureResponse> purchaseFurniture(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long furnitureId) {
        validateUserId(userId);
        return ApiResponse.success(
                RoomSuccessCode.FURNITURE_PURCHASED,
                roomService.purchaseFurniture(userId, furnitureId));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(RoomErrorCode.USER_ID_REQUIRED);
        }
    }
}

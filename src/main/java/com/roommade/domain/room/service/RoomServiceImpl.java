package com.roommade.domain.room.service;

import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.domain.room.code.RoomErrorCode;
import com.roommade.domain.room.dto.response.FurnitureRewardResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardSourceResponse;
import com.roommade.domain.room.dto.response.FurnitureRewardsResponse;
import com.roommade.domain.room.dto.response.RoomFurnitureResponse;
import com.roommade.domain.room.dto.response.RoomResponse;
import com.roommade.domain.room.mapper.RoomMapper;
import com.roommade.global.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private static final List<Integer> FURNITURE_REWARD_STAGES =
            List.of(15, 30, 45, 60, 75);

    private final RoomMapper roomMapper;
    private final PreparationService preparationService;

    @Override
    @Transactional
    public RoomResponse getRoom(Long userId) {
        ReadinessDiagnosisResponse readiness = synchronizeRewards(userId);
        return new RoomResponse(
                readiness.getReadinessScore(),
                roomMapper.findOwnedFurnitureByUserId(userId));
    }

    @Override
    @Transactional
    public FurnitureRewardsResponse getPendingRewards(Long userId) {
        synchronizeRewards(userId);
        List<FurnitureRewardResponse> rewards = roomMapper.findPendingRewardsByUserId(userId)
                .stream()
                .map(reward -> new FurnitureRewardResponse(
                        reward.getRewardId(),
                        reward.getRewardStage(),
                        reward.getGrantedAt(),
                        roomMapper.findSelectableFurniture(
                                userId, reward.getRewardStage())))
                .collect(Collectors.toList());
        return new FurnitureRewardsResponse(rewards);
    }

    @Override
    @Transactional
    public RoomFurnitureResponse claimReward(
            Long userId, Long rewardId, Long furnitureId) {
        FurnitureRewardSourceResponse reward =
                roomMapper.findPendingRewardForUpdate(userId, rewardId);
        if (reward == null) {
            throw new BusinessException(RoomErrorCode.FURNITURE_REWARD_NOT_AVAILABLE);
        }
        if (!roomMapper.existsSelectableFurniture(
                userId, furnitureId, reward.getRewardStage())) {
            throw new BusinessException(RoomErrorCode.FURNITURE_NOT_SELECTABLE);
        }

        roomMapper.unplaceFurnitureInSameCategory(userId, furnitureId);
        if (roomMapper.insertUserFurniture(userId, furnitureId) == 0) {
            throw new BusinessException(RoomErrorCode.FURNITURE_NOT_SELECTABLE);
        }
        if (roomMapper.claimReward(userId, rewardId, furnitureId) == 0) {
            throw new BusinessException(RoomErrorCode.FURNITURE_REWARD_NOT_AVAILABLE);
        }
        return roomMapper.findOwnedFurnitureById(userId, furnitureId);
    }

    @Override
    @Transactional
    public RoomFurnitureResponse updatePlacement(
            Long userId, Long furnitureId, boolean placed) {
        if (!roomMapper.existsOwnedFurniture(userId, furnitureId)) {
            throw new BusinessException(RoomErrorCode.FURNITURE_NOT_OWNED);
        }
        if (placed) {
            roomMapper.unplaceFurnitureInSameCategory(userId, furnitureId);
        }
        roomMapper.updateFurniturePlacement(userId, furnitureId, placed);
        return roomMapper.findOwnedFurnitureById(userId, furnitureId);
    }

    @Override
    @Transactional
    public void grantAllBasicFurniture(Long userId) {
        roomMapper.insertAllMissingBasicFurniture(userId);
    }

    private ReadinessDiagnosisResponse synchronizeRewards(Long userId) {
        ReadinessDiagnosisResponse readiness =
                preparationService.getReadinessDiagnosis(userId);
        roomMapper.insertInitialFurniture(userId);

        if (readiness.getIndependenceStatus() != IndependenceStatus.PREPARING) {
            roomMapper.insertAllMissingBasicFurniture(userId);
            return readiness;
        }

        BigDecimal score = readiness.getReadinessScore();
        for (int stage : FURNITURE_REWARD_STAGES) {
            if (score.compareTo(BigDecimal.valueOf(stage)) >= 0) {
                roomMapper.insertRewardIfAbsent(userId, stage);
            }
        }
        return readiness;
    }
}

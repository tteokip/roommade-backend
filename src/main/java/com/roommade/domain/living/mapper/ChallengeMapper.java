package com.roommade.domain.living.mapper;

import com.roommade.domain.living.dto.response.ChallengeLevelResponse;
import com.roommade.domain.living.dto.response.ChallengeRewardResponse;
import com.roommade.domain.living.dto.response.LatestChallengeResultResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChallengeMapper {

    List<ChallengeLevelResponse> findAllLevels();

    /** 마감 안 된 지난 날짜의 daily_living_costs를 daily_challenges로 확정하고, 마감된 건수를 반환한다. */
    int closeDueChallenges(@Param("today") LocalDate today, @Param("closedAt") LocalDateTime closedAt);

    /** 방금 마감된 것 중 달성 레벨이 있는(코인 지급 대상인) 것만 조회한다. */
    List<ChallengeRewardResponse> findRewardsClosedAt(@Param("closedAt") LocalDateTime closedAt);

    /** 사용자의 가장 최근 마감된 챌린지 결과를 조회한다. 마감 기록이 없으면 null. */
    LatestChallengeResultResponse findLatestResult(@Param("userId") Long userId);
}

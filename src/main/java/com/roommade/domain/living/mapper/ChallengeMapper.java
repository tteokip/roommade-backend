package com.roommade.domain.living.mapper;

import com.roommade.domain.living.dto.response.ChallengeLevelResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChallengeMapper {

    List<ChallengeLevelResponse> findAllLevels();
}

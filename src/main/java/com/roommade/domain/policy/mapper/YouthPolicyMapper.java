package com.roommade.domain.policy.mapper;

import com.roommade.domain.policy.domain.YouthPolicy;
import com.roommade.domain.policy.domain.YouthPolicyRegion;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface YouthPolicyMapper {
    int upsert(YouthPolicy youthPolicy);
    Long findYouthPolicyIdByPolicyNo(@Param("policyNo") String policyNo);
    int deleteRegionsByYouthPolicyId(@Param("youthPolicyId") Long youthPolicyId);
    int insertRegions(@Param("youthPolicyId") Long youthPolicyId, @Param("regions") List<YouthPolicyRegion> regions);
}

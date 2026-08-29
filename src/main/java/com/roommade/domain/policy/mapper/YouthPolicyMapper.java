package com.roommade.domain.policy.mapper;

import com.roommade.domain.policy.domain.YouthPolicy;
import com.roommade.domain.policy.domain.YouthPolicyRegion;
import com.roommade.domain.policy.dto.response.YouthPolicyDetailResponse;
import com.roommade.domain.policy.dto.response.YouthPolicyListResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface YouthPolicyMapper {
    int upsert(YouthPolicy youthPolicy);
    Long findYouthPolicyIdByPolicyNo(@Param("policyNo") String policyNo);
    int deleteRegionsByYouthPolicyId(@Param("youthPolicyId") Long youthPolicyId);
    int insertRegions(@Param("youthPolicyId") Long youthPolicyId, @Param("regions") List<YouthPolicyRegion> regions);
    List<YouthPolicyListResponse> findYouthPolicies(
            @Param("regionCode") String regionCode, @Param("age") Integer age,
            @Param("income") Long income, @Param("offset") long offset, @Param("size") int size);
    long countYouthPolicies(@Param("regionCode") String regionCode, @Param("age") Integer age, @Param("income") Long income);
    YouthPolicyDetailResponse findYouthPolicyById(@Param("youthPolicyId") Long youthPolicyId);
}

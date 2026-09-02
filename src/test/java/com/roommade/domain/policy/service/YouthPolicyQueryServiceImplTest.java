package com.roommade.domain.policy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.policy.code.YouthPolicyErrorCode;
import com.roommade.domain.policy.mapper.YouthPolicyMapper;
import com.roommade.domain.user.dto.response.UserPolicyProfileResponse;
import com.roommade.domain.user.mapper.UserProfileMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YouthPolicyQueryServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private YouthPolicyMapper youthPolicyMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private YouthPolicyQueryServiceImpl youthPolicyQueryService;

    @Test
    void filtersPoliciesWithAgeAndAnnualIncomeConvertedFromUserProfile() {
        UserPolicyProfileResponse profile = new UserPolicyProfileResponse(
                "영헌", LocalDate.now().minusYears(25).minusDays(1), 2_500_000L);
        when(userProfileMapper.findPolicyProfileByUserId(USER_ID)).thenReturn(profile);
        when(youthPolicyMapper.countYouthPolicies("11", 25, 3_000L)).thenReturn(0L);
        when(youthPolicyMapper.findYouthPolicies("11", 25, 3_000L, 0L, 10)).thenReturn(List.of());

        assertThat(youthPolicyQueryService.getYouthPolicies(USER_ID, "11", 1, 10).getUserName())
                .isEqualTo("영헌");

        verify(youthPolicyMapper).countYouthPolicies("11", 25, 3_000L);
        verify(youthPolicyMapper).findYouthPolicies("11", 25, 3_000L, 0L, 10);
    }

    @Test
    void throwsWhenUserProfileDoesNotExist() {
        when(userProfileMapper.findPolicyProfileByUserId(USER_ID)).thenReturn(null);

        assertThatThrownBy(() -> youthPolicyQueryService.getYouthPolicies(USER_ID, "11", 1, 10))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(YouthPolicyErrorCode.USER_PROFILE_NOT_FOUND);
    }
}

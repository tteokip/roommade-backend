package com.roommade.domain.user.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPolicyProfileResponse {
    private String name;
    private LocalDate birthDate;
    private Long monthlyIncome;
}

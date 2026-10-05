package com.roommade.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupResponse {

    private final Long userId;
    private final String email;
}

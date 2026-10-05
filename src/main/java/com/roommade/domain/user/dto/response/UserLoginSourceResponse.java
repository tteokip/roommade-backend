package com.roommade.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginSourceResponse {

    private Long userId;
    private String email;
    private String passwordHash;
}

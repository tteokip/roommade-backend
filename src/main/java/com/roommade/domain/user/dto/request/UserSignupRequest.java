package com.roommade.domain.user.dto.request;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotNull
    @PositiveOrZero
    private Long monthlyIncome;

    @Size(max = 255)
    private String workplaceRoadAddress;

    @Size(max = 255)
    private String workplaceDetailAddress;

    @NotNull
    @PositiveOrZero
    private Long depositLimit;

    @NotNull
    @PositiveOrZero
    private Long monthlyRentLimit;
}

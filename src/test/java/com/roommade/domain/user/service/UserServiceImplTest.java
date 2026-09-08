package com.roommade.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.roommade.domain.user.code.UserErrorCode;
import com.roommade.domain.user.dto.request.UserSignupRequest;
import com.roommade.domain.user.dto.request.UserLoginRequest;
import com.roommade.domain.user.dto.response.UserLoginSourceResponse;
import com.roommade.domain.user.dto.response.UserSignupResponse;
import com.roommade.domain.user.mapper.UserMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userMapper, passwordEncoder);
    }

    @Test
    void signsUpUserAndInitializesRequiredUserData() {
        UserSignupRequest request = signupRequest();
        given(userMapper.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("hashed-password");
        given(userMapper.findIdByEmail(request.getEmail())).willReturn(7L);

        UserSignupResponse response = userService.signup(request);

        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        then(userMapper).should().insertUser(request.getEmail(), "hashed-password");
        then(userMapper).should().insertProfile(7L, request);
        then(userMapper).should().insertCoinWallet(7L);
        then(userMapper).should().insertIndependenceProgress(7L);
    }

    @Test
    void rejectsSignupWhenEmailAlreadyExists() {
        UserSignupRequest request = signupRequest();
        given(userMapper.existsByEmail(request.getEmail())).willReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(UserErrorCode.EMAIL_ALREADY_EXISTS);

        then(userMapper).should().existsByEmail(request.getEmail());
        then(userMapper).shouldHaveNoMoreInteractions();
        then(passwordEncoder).shouldHaveNoInteractions();
    }

    @Test
    void logsInWhenEmailAndPasswordMatch() {
        UserLoginRequest request = new UserLoginRequest("member@roommade.com", "password123");
        UserLoginSourceResponse user = new UserLoginSourceResponse(7L, request.getEmail(), "hashed-password");
        given(userMapper.findLoginUserByEmail(request.getEmail())).willReturn(user);
        given(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).willReturn(true);

        assertThat(userService.login(request).getUserId()).isEqualTo(7L);
        then(passwordEncoder).should().matches(request.getPassword(), user.getPasswordHash());
    }

    @Test
    void rejectsLoginWhenPasswordDoesNotMatch() {
        UserLoginRequest request = new UserLoginRequest("member@roommade.com", "wrong-password");
        UserLoginSourceResponse user = new UserLoginSourceResponse(7L, request.getEmail(), "hashed-password");
        given(userMapper.findLoginUserByEmail(request.getEmail())).willReturn(user);
        given(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).willReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void rejectsLoginWhenEmailDoesNotExist() {
        UserLoginRequest request = new UserLoginRequest("unknown@roommade.com", "password123");
        given(userMapper.findLoginUserByEmail(request.getEmail())).willReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_CREDENTIALS);

        then(passwordEncoder).shouldHaveNoInteractions();
    }

    private UserSignupRequest signupRequest() {
        return new UserSignupRequest(
                "member@roommade.com",
                "password123",
                "룸메이드",
                LocalDate.of(2000, 1, 1),
                3_000_000L,
                "서울특별시 영등포구 국제금융로 8길",
                "101호",
                50_000_000L,
                700_000L);
    }
}

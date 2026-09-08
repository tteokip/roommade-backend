package com.roommade.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.roommade.domain.user.dto.request.UserLoginRequest;
import com.roommade.domain.user.dto.response.UserLoginResponse;
import com.roommade.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void storesAuthenticatedUserIdInSessionWhenLoginSucceeds() {
        UserLoginRequest request = new UserLoginRequest("member@roommade.com", "password123");
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        given(userService.login(request)).willReturn(new UserLoginResponse(7L, request.getEmail()));

        userController.login(request, httpRequest);

        assertThat(httpRequest.getSession(false).getAttribute("userId")).isEqualTo(7L);
    }

    @Test
    void invalidatesExistingSessionOnLogout() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        httpRequest.setSession(session);

        userController.logout(httpRequest);

        assertThat(session.isInvalid()).isTrue();
    }
}

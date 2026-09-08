package com.roommade.domain.user.service;

import com.roommade.domain.user.code.UserErrorCode;
import com.roommade.domain.user.dto.request.UserSignupRequest;
import com.roommade.domain.user.dto.request.UserLoginRequest;
import com.roommade.domain.user.dto.response.UserLoginResponse;
import com.roommade.domain.user.dto.response.UserLoginSourceResponse;
import com.roommade.domain.user.dto.response.UserSignupResponse;
import com.roommade.domain.user.mapper.UserMapper;
import com.roommade.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            userMapper.insertUser(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Long userId = userMapper.findIdByEmail(request.getEmail());
        userMapper.insertProfile(userId, request);
        userMapper.insertCoinWallet(userId);
        userMapper.insertIndependenceProgress(userId);

        return new UserSignupResponse(userId, request.getEmail());
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        UserLoginSourceResponse user = userMapper.findLoginUserByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        return new UserLoginResponse(user.getUserId(), user.getEmail());
    }
}

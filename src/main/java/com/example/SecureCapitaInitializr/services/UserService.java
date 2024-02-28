package com.example.SecureCapitaInitializr.services;

import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.NewPasswordForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;

public interface UserService {
    UserResponse create(UserRequest request);
    void login(LoginForm form);

    UserResponse getByEmail(String email);

    void sendVerificationCode(UserResponse userResponse);

    UserResponse verifyCode(String email, String code);

    void resetPassword(String email);

    UserResponse verifyPasswordKey(String key);

    void updatePasswordWithKey(String key, NewPasswordForm form);
}

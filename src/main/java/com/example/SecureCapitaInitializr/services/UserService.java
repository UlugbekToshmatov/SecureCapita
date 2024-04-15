package com.example.SecureCapitaInitializr.services;

import com.example.SecureCapitaInitializr.dtos.user.*;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserResponse create(UserRegistrationForm request);
    void verifyAccountKey(String key, String email);


    UserResponse login(LoginForm form, HttpServletRequest request);
    void sendMfaVerificationCode(UserResponse user);
    UserResponse verifyMfaCode(String email, String code, HttpServletRequest request);


    void resetPassword(String email);
    UserResponse verifyPasswordKey(String key);
    void updatePasswordWithKey(String key, NewPasswordForm form);


    UserResponse getByEmail(String email);


    UserResponse refreshAccessToken(HttpServletRequest request);


    void logout(Long userId);

    UserResponse updateUserDetails(Long userId, UpdateForm form);
}

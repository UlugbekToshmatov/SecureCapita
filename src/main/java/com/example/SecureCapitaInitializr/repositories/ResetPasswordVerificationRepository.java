package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.resetPasswordVerification.ResetPasswordVerification;
import com.example.SecureCapitaInitializr.models.user.UserWithRole;

import java.time.LocalDateTime;

public interface ResetPasswordVerificationRepository<T extends ResetPasswordVerification> {
    void deletePasswordVerificationCodesByUserId(Long userId);

    void insertPasswordVerificationCode(Long userId, String verificationUrl, LocalDateTime expirationDate);

    UserWithRole verifyPasswordKey(String verificationUrl);

    T getVerificationByUrl(String verificationUrl);
}

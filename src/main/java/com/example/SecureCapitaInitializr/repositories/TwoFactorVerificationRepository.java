package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.twoFactorVerification.TwoFactorVerification;

import java.time.LocalDateTime;

public interface TwoFactorVerificationRepository<T extends TwoFactorVerification> {
    T getCodeByUserId(Long userId);
    void deleteVerificationCodesByUserId(Long userId);

    void insertVerificationCode(Long userId, String verificationCode, LocalDateTime expirationDate);
}

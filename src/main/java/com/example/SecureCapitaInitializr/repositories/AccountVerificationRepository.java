package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.AccountVerification;

public interface AccountVerificationRepository<T extends AccountVerification> {
    void saveActivationLink(Long userId, String verificationUrl);
}

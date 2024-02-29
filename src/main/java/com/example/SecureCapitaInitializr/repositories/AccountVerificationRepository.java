package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.accountverification.AccountVerification;

public interface AccountVerificationRepository<T extends AccountVerification> {
    void saveActivationLink(Long userId, String verificationUrl);
    T getAccountVerificationByUserId(Long userId);
    void deleteAccountVerificationUrlByUserId(Long userId);
}

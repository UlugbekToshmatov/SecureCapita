package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.models.AccountVerification;
import com.example.SecureCapitaInitializr.repositories.AccountVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.AccountVerificationQuery.INSERT_ACCOUNT_VERIFICATION_URL_QUERY;

@Repository
@RequiredArgsConstructor
public class AccountVerificationRepositoryImpl implements AccountVerificationRepository<AccountVerification> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void saveActivationLink(Long userId, String verificationUrl) {
        jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", userId, "url", verificationUrl));
    }
}

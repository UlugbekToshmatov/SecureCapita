package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.models.TwoFactorVerification;
import com.example.SecureCapitaInitializr.repositories.TwoFactorVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.TwoFactorVerificationQuery.DELETE_VERIFICATION_CODES_QUERY;
import static com.example.SecureCapitaInitializr.repositories.queries.TwoFactorVerificationQuery.INSERT_VERIFICATION_CODE_QUERY;

@Repository
@RequiredArgsConstructor
public class TwoFactorVerificationRepositoryImpl implements TwoFactorVerificationRepository<TwoFactorVerification> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void deleteVerificationCodesByUserId(Long userId) {
        jdbc.update(DELETE_VERIFICATION_CODES_QUERY, Map.of("userId", userId));
    }

    @Override
    public void insertVerificationCode(Long userId, String verificationCode, LocalDateTime expirationDate) {
        SqlParameterSource parameters = getParameterSource(userId, verificationCode, expirationDate);
        jdbc.update(INSERT_VERIFICATION_CODE_QUERY, parameters);
    }

    private MapSqlParameterSource getParameterSource(Long userId, String verificationCode, LocalDateTime expirationDate) {
        return new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "code", verificationCode,
                "expirationDate", expirationDate
            )
        );
    }
}

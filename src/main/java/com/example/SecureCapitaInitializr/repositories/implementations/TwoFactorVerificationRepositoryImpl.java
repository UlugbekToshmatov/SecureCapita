package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.TwoFactorVerification;
import com.example.SecureCapitaInitializr.repositories.TwoFactorVerificationRepository;
import com.example.SecureCapitaInitializr.rowmappers.TwoFactorVerificationRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.TwoFactorVerificationQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TwoFactorVerificationRepositoryImpl implements TwoFactorVerificationRepository<TwoFactorVerification> {
    private final NamedParameterJdbcTemplate jdbc;


    @Override
    public TwoFactorVerification getCodeByUserId(Long userId) {
        try {
            return jdbc.queryForObject(
                GET_VERIFICATION_CODE_BY_USER_ID_QUERY,
                Map.of("userId", userId),
                new TwoFactorVerificationRowMapper()
            );
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Verification code for user with id=" + userId + " not found");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

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

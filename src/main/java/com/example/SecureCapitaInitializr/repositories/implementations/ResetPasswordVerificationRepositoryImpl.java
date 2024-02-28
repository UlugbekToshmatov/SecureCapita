package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.resetPasswordVerification.ResetPasswordVerification;
import com.example.SecureCapitaInitializr.models.user.UserWithRole;
import com.example.SecureCapitaInitializr.models.user.UserWithVerificationUrl;
import com.example.SecureCapitaInitializr.repositories.ResetPasswordVerificationRepository;
import com.example.SecureCapitaInitializr.rowmappers.ResetPasswordVerificationRowMapper;
import com.example.SecureCapitaInitializr.rowmappers.UserWithVerificationUrlRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.ResetPasswordVerificationQuery.*;

@Repository
@RequiredArgsConstructor
public class ResetPasswordVerificationRepositoryImpl implements ResetPasswordVerificationRepository<ResetPasswordVerification> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void deletePasswordVerificationCodesByUserId(Long userId) {
        jdbc.update(DELETE_PASSWORD_VERIFICATION_CODES_BY_USER_ID_QUERY, Map.of("userId", userId));
    }

    @Override
    public void insertPasswordVerificationCode(Long userId, String verificationUrl, LocalDateTime expirationDate) {
        jdbc.update(INSERT_PASSWORD_VERIFICATION_CODE_QUERY,
            Map.of("userId", userId, "url", verificationUrl, "expirationDate", expirationDate)
        );
    }

    @Override
    public UserWithRole verifyPasswordKey(String verificationUrl) {
        try {
            UserWithVerificationUrl userWithVerificationUrl = jdbc.queryForObject(SELECT_USER_WITH_ROLE_BY_URL, Map.of("url", verificationUrl), new UserWithVerificationUrlRowMapper());
            if (userWithVerificationUrl == null)
                throw new EmptyResultDataAccessException(1);
            if (userWithVerificationUrl.getUrlExpirationDate().isBefore(LocalDateTime.now()))
                throw new ApiException("This link has expired. Please reset your password again.");

            return userWithVerificationUrl.getUserWithRole();
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Invalid key");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again.");
        }
    }

    @Override
    public ResetPasswordVerification getVerificationByUrl(String verificationUrl) {
        try {
            return jdbc.queryForObject(SELECT_ALL_BY_URL_QUERY, Map.of("url", verificationUrl), new ResetPasswordVerificationRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Invalid key");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again.");
        }
    }
}

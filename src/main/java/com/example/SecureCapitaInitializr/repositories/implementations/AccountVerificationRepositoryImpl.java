package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.accountverification.AccountVerification;
import com.example.SecureCapitaInitializr.repositories.AccountVerificationRepository;
import com.example.SecureCapitaInitializr.rowmappers.AccountVerificationRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.AccountVerificationQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AccountVerificationRepositoryImpl implements AccountVerificationRepository<AccountVerification> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void saveActivationLink(Long userId, String verificationUrl) {
        jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", userId, "url", verificationUrl));
    }

    @Override
    public AccountVerification getAccountVerificationByUserId(Long userId) {
        try {
            return jdbc.queryForObject(SELECT_BY_USER_ID_QUERY, Map.of("userId", userId), new AccountVerificationRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Account verification url by userId=" + userId + " not found");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

    @Override
    public void deleteAccountVerificationUrlByUserId(Long userId) {
        jdbc.update(SET_DELETED_TRUE_BY_USER_ID_QUERY, Map.of("userId", userId));
    }
}

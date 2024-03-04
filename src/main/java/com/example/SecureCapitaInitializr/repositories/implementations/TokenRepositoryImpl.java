package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.enums.TokenType;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.token.Token;
import com.example.SecureCapitaInitializr.repositories.TokenRepository;
import com.example.SecureCapitaInitializr.rowmappers.TokenRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.TokenQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenRepositoryImpl implements TokenRepository<Token> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void insertTokens(Long userId, String accessToken, String refreshToken, Date accessTokenExpiration, Date refreshTokenExpiration) {
        SqlParameterSource sqlParameterSource = getSqlParameters(userId, accessToken, accessTokenExpiration, refreshToken, refreshTokenExpiration);
        jdbc.update(INSERT_TOKENS_QUERY, sqlParameterSource);
    }

    @Override
    public void insertToken(Long userId, String accessToken, Date expiresAt) {
        SqlParameterSource parameters = getParameters(userId, TokenType.ACCESS_TOKEN, accessToken, expiresAt);
        jdbc.update(INSERT_TOKEN_QUERY, parameters);
    }

    @Override
    public Token getByToken(String token) {
        try {
            return jdbc.queryForObject(SELECT_BY_TOKEN_QUERY, Map.of("token", token), new TokenRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No such token found");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

    @Override
    public void expireToken(String token) {
        jdbc.update(EXPIRE_BY_TOKEN_QUERY, Map.of("token", token));
    }

    @Override
    public void revokeToken(String token) {
        jdbc.update(REVOKE_BY_TOKEN_QUERY, Map.of("token", token));
    }

    @Override
    public void revokeAllTokensByUserId(Long userId) {
        jdbc.update(REVOKE_ALL_TOKENS_BY_USER_ID_QUERY, Map.of("userId", userId));
    }

    @Override
    public void revokeAccessTokensByUserId(Long userId) {
        jdbc.update(REVOKE_ALL_ACCESS_TOKENS_BY_USER_ID_QUERY, Map.of("userId", userId, "type", TokenType.ACCESS_TOKEN.name()));
    }

    @Override
    public Boolean isTokenNotRevoked(String token) {
        try {
            return jdbc.queryForObject(SELECT_COUNT_BY_TOKEN_QUERY, Map.of("token", token), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No such token found!");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

    private SqlParameterSource getParameters(Long userId, TokenType type, String token, Date expiresAt) {
        return new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "type", type.name(),
                "token", token,
                "expiresAt", expiresAt
            )
        );
    }

    private SqlParameterSource getSqlParameters(Long userId, String accessToken, Date accessTokenExpiration, String refreshToken, Date refreshTokenExpiration) {
        return new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "accessToken", accessToken,
                "accessTokenType", TokenType.ACCESS_TOKEN.name(),
                "accessTokenExpiration", accessTokenExpiration,
                "refreshToken", refreshToken,
                "refreshTokenType", TokenType.REFRESH_TOKEN.name(),
                "refreshTokenExpiration", refreshTokenExpiration
            )
        );
    }

}

package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.token.Token;

import java.util.Date;

public interface TokenRepository<T extends Token> {
    void insertTokens(Long userId, String accessToken, String refreshToken, Date accessTokenExpiration, Date refreshTokenExpiration);
    void insertToken(Long userId, String accessToken, Date expiresAt);
    T getByToken(String token);
    void expireToken(String token);
    void revokeToken(String token);
    void revokeAllTokensByUserId(Long userId);
    void revokeAccessTokensByUserId(Long userId);
    Boolean isTokenNotRevoked(String token);
}

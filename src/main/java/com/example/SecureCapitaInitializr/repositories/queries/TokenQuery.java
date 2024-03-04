package com.example.SecureCapitaInitializr.repositories.queries;

public class TokenQuery {
    public static final String SELECT_BY_TOKEN_QUERY = """
            SELECT * FROM tokens WHERE token=:token AND revoked=false AND expired=false
        """;
    public static final String EXPIRE_BY_TOKEN_QUERY = """
            UPDATE tokens SET expired=TRUE
            WHERE user_id=(SELECT user_id FROM tokens WHERE token=:token) AND expired=false AND revoked=false
        """;

    public static final String REVOKE_BY_TOKEN_QUERY = """
            UPDATE tokens SET revoked=TRUE
            WHERE user_id=(SELECT user_id FROM tokens WHERE token=:token) AND expired=false AND revoked=false
        """;

    public static final String INSERT_TOKEN_QUERY = """
            INSERT INTO tokens (token, type, expires_at, user_id)
            VALUES (:token, :type, :expiresAt, :userId)
        """;

    public static final String INSERT_TOKENS_QUERY = """
            INSERT INTO tokens (token, type, expires_at, user_id) VALUES
            (:accessToken, :accessTokenType, :accessTokenExpiration, :userId),
            (:refreshToken, :refreshTokenType, :refreshTokenExpiration, :userId)
        """;

    public static final String REVOKE_ALL_TOKENS_BY_USER_ID_QUERY = """
            UPDATE tokens SET revoked=true
            WHERE user_id=:userId AND expired=false AND revoked=false
        """;

    public static final String REVOKE_ALL_ACCESS_TOKENS_BY_USER_ID_QUERY = """
            UPDATE tokens SET revoked=true
            WHERE user_id=:userId AND type=:type AND expired=false AND revoked=false
        """;

    public static final String SELECT_COUNT_BY_TOKEN_QUERY = """
            SELECT count(*) > 0 FROM tokens
            WHERE token=:token AND revoked=false AND expired=false
        """;
}

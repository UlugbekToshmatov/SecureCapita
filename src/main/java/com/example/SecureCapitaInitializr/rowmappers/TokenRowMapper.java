package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.enums.TokenType;
import com.example.SecureCapitaInitializr.models.token.Token;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenRowMapper implements RowMapper<Token> {
    @Override
    public Token mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Token.builder()
            .id(resultSet.getLong("id"))
            .userId(resultSet.getLong("user_id"))
            .token(resultSet.getString("token"))
            .type(TokenType.valueOf(resultSet.getString("type")))
            .issuedAt(resultSet.getTimestamp("issued_at").toLocalDateTime())
            .expiresAt(resultSet.getTimestamp("expires_at").toLocalDateTime())
            .build();
    }
}

package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.models.resetPasswordVerification.ResetPasswordVerification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetPasswordVerificationRowMapper implements RowMapper<ResetPasswordVerification> {
    @Override
    public ResetPasswordVerification mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return ResetPasswordVerification.builder()
            .id(resultSet.getLong("id"))
            .userId(resultSet.getLong("user_id"))
            .url(resultSet.getString("url"))
            .expirationDate(resultSet.getTimestamp("expiration_date").toLocalDateTime())
            .build();
    }
}

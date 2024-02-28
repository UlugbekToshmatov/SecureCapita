package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.models.twoFactorVerification.TwoFactorVerification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TwoFactorVerificationRowMapper implements RowMapper<TwoFactorVerification> {

    @Override
    public TwoFactorVerification mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return TwoFactorVerification.builder()
            .id(resultSet.getLong("id"))
            .userId(resultSet.getLong("user_id"))
            .code(resultSet.getString("code"))
            .expirationDate(resultSet.getTimestamp("expiration_date").toLocalDateTime())
            .createdDate(resultSet.getTimestamp("created_date").toLocalDateTime())
            .modifiedDate(resultSet.getTimestamp("modified_date").toLocalDateTime())
            .build();
    }
}

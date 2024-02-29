package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.models.accountverification.AccountVerification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountVerificationRowMapper implements RowMapper<AccountVerification> {
    @Override
    public AccountVerification mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return AccountVerification.builder()
            .id(resultSet.getLong("id"))
            .userId(resultSet.getLong("user_id"))
            .url(resultSet.getString("url"))
            .build();
    }
}

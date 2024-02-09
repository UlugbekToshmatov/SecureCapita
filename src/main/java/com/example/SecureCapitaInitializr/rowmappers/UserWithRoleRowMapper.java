package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.models.user.UserWithRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserWithRoleRowMapper implements RowMapper<UserWithRole> {
    @Override
    public UserWithRole mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return UserWithRole.builder()
            .id(resultSet.getLong("id"))
            .firstName(resultSet.getString("first_name"))
            .lastName(resultSet.getString("last_name"))
            .email(resultSet.getString("email"))
            .password(resultSet.getString("password"))
            .address(resultSet.getString("address"))
            .phone(resultSet.getString("phone"))
            .title(resultSet.getString("title"))
            .bio(resultSet.getString("bio"))
            .imageUrl(resultSet.getString("image_url"))
            .enabled(resultSet.getBoolean("enabled"))
            .locked(resultSet.getBoolean("locked"))
            .usingMfa(resultSet.getBoolean("using_mfa"))
            .createdDate(resultSet.getTimestamp("created_date").toLocalDateTime())
            .modifiedDate(resultSet.getTimestamp("modified_date").toLocalDateTime())
            .roleName(resultSet.getString("name"))
            .permission(resultSet.getString("permission"))
            .build();
    }
}

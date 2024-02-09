package com.example.SecureCapitaInitializr.rowmappers;

import com.example.SecureCapitaInitializr.models.role.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
//        Role role = new Role();
//        role.setId(resultSet.getInt("id"));
//        role.setName(resultSet.getString("name"));
//        role.setPermission(resultSet.getString("permission"));
        return Role.builder()
            .id(resultSet.getInt("id"))
            .name(resultSet.getString("name"))
            .permission(resultSet.getString("permission"))
            .build();
//        return role;
    }
}

package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.Role;
import com.example.SecureCapitaInitializr.repositories.RoleRepository;
import com.example.SecureCapitaInitializr.rowmappers.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.example.SecureCapitaInitializr.enums.RoleType.ROLE_USER;
import static com.example.SecureCapitaInitializr.repositories.queries.RoleQuery.FIND_ROLE_BY_ID_QUERY;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role findByNameAndDeletedFalse(String name) {
        try {
            return jdbc.queryForObject(FIND_ROLE_BY_ID_QUERY, Map.of("name", name), new RoleRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        }
    }
}

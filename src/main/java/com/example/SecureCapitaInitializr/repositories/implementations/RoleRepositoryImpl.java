package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.role.Role;
import com.example.SecureCapitaInitializr.repositories.RoleRepository;
import com.example.SecureCapitaInitializr.rowmappers.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.example.SecureCapitaInitializr.enums.RoleType.ROLE_USER;
import static com.example.SecureCapitaInitializr.repositories.queries.RoleQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private final NamedParameterJdbcTemplate jdbc;


    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list() {
        log.info("Fetching all roles");
        try {
            return jdbc.query(SELECT_ALL_QUERY, new RoleRowMapper());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public Role findByNameAndDeletedFalse(String name) {
        log.info("Fetching role by name: '{}'", name);
        try {
            return jdbc.queryForObject(FIND_ROLE_BY_NAME_QUERY, Map.of("name", name), new RoleRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Fetching role by user id={}", userId);
        try {
            return jdbc.queryForObject(FIND_ROLE_BY_USER_ID_QUERY, Map.of("userId", userId), new RoleRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by user id=" + userId);
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void updateUserRole(Long userId, Integer roleId) {
        log.info("Updating role of user with id={} to role id={}", userId, roleId);
        jdbc.update(UPDATE_ROLE_BY_USER_ID_QUERY, Map.of("userId", userId, "roleId", roleId));
    }
}

package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.user.User;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import com.example.SecureCapitaInitializr.models.user.UserWithRole;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import com.example.SecureCapitaInitializr.rowmappers.UserRowMapper;
import com.example.SecureCapitaInitializr.rowmappers.UserWithRoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.UserQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public User create(User user) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource parameters = getSqlParameterSource(user);
        jdbc.update(INSERT_INTO_USERS_QUERY, parameters, holder);
        user.setCreatedDate(LocalDateTime.now());
        Long id = (Long) requireNonNull(holder.getKeys()).get("id");
        user.setId(id);
        user.setEnabled(Boolean.FALSE);
        user.setLocked(Boolean.FALSE);
        user.setUsingMfa(Boolean.FALSE);
        return user;
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    public Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USERS_BY_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Loading user with email " + email);
            UserWithRole user = jdbc.queryForObject(SELECT_BY_EMAIL_QUERY, Map.of("email", email), new UserWithRoleRowMapper() /*UserWithRole.class*/);
            return new UserPrincipal(user);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User with email " + email + " not found");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

    @Override
    public User findByEmailAndDeletedFalse(String email) {
        try {
            log.info("Getting user with email " + email);
            return jdbc.queryForObject(SELECT_ALL_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper() /*User.class*/);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User with email " + email + " not found");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please, try again later.");
        }
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource(
            Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail().trim().toLowerCase(),
                "password", user.getPassword(),
                "roleId", user.getRoleId()
            )
        );
    }
}

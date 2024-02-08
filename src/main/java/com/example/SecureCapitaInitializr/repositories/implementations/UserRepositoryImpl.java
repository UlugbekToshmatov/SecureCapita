package com.example.SecureCapitaInitializr.repositories.implementations;

import com.example.SecureCapitaInitializr.models.User;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static com.example.SecureCapitaInitializr.repositories.queries.UserQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {
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

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource(
            Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "roleId", user.getRoleId()
            )
        );
    }
}

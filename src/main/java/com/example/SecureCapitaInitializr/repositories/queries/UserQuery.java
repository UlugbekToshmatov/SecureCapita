package com.example.SecureCapitaInitializr.repositories.queries;

public class UserQuery {
    public static String COUNT_USERS_BY_EMAIL_QUERY = """
            SELECT COUNT(*) FROM users
            WHERE email=:email AND deleted=false
        """;
    public static String INSERT_INTO_USERS_QUERY = """
            INSERT INTO users (first_name, last_name, email, password, role_id)
            VALUES (:firstName, :lastName, :email, :password, :roleId)
        """;
}

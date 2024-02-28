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

    public static String SELECT_BY_EMAIL_QUERY = """
                SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission
                FROM users AS u JOIN roles AS r ON u.role_id=r.id
                WHERE u.email=:email AND u.deleted=false
            """;

    public static String SELECT_ALL_BY_EMAIL_QUERY = """
                SELECT * FROM users
                WHERE email=:email AND deleted=false
            """;

    public static final String UPDATE_PASSWORD_BY_ID = """
            UPDATE users SET password=:newPassword WHERE id=:userId
        """;
}

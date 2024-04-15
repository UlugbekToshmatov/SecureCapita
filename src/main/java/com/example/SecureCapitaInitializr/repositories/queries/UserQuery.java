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

    public static final String INSERT_INTO_USERS_WITH_MFA_ENABLED_QUERY = """
            INSERT INTO users (first_name, last_name, email, password, role_id, using_mfa)
            VALUES (:firstName, :lastName, :email, :password, :roleId, :usingMfa)
        """;

    public static String SELECT_USER_WITH_ROLE_BY_EMAIL_QUERY = """
            SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission
            FROM users AS u JOIN roles AS r ON u.role_id=r.id
            WHERE u.email=:email AND u.deleted=false
        """;

    public static String SELECT_USER_WITH_ROLE_BY_USER_ID_QUERY = """
            SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission
            FROM users AS u JOIN roles AS r ON u.role_id=r.id
            WHERE u.id=:userId AND u.deleted=false
        """;

    public static String SELECT_ALL_BY_EMAIL_QUERY = """
            SELECT * FROM users
            WHERE email=:email AND deleted=false
        """;

    public static final String UPDATE_PASSWORD_BY_ID = """
            UPDATE users SET password=:confirmPassword, modified_date=CURRENT_TIMESTAMP
            WHERE id=:userId AND deleted=false
        """;

    public static final String UPDATE_ENABLED_BY_USER_ID_QUERY = """
            UPDATE users SET enabled=true, modified_date=CURRENT_TIMESTAMP
            WHERE id=:userId AND deleted=false
        """;

    public static final String EXISTS_BY_USER_ID_QUERY = """
            SELECT count(*) > 0 AS exists FROM users
            WHERE id=:userId AND deleted=false
        """;

    public static final String UPDATE_USER_DETAILS_BY_USER_ID_QUERY = """
            UPDATE users AS u
            SET first_name=:firstName, last_name=:lastName, email=:email, phone=:phone, address=:address, title=:title, bio=:bio, modified_date=CURRENT_TIMESTAMP
            FROM roles AS r
            WHERE u.id = :id AND u.deleted = false AND u.role_id = r.id
            RETURNING u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission;
        """;

    public static final String UPDATE_USER_PASSWORD_BY_USER_ID_QUERY = """
            UPDATE users AS u
            SET password=:newPassword, modified_date=CURRENT_TIMESTAMP
            FROM roles AS r
            WHERE u.id = :id AND u.deleted = false AND u.role_id = r.id
            RETURNING u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission;
        """;
}

package com.example.SecureCapitaInitializr.repositories.queries;

public class ResetPasswordVerificationQuery {
    public final static String SELECT_ALL_BY_URL_QUERY = """
            SELECT * FROM reset_password_verifications WHERE url=:url AND deleted=false
        """;

    public static final String DELETE_PASSWORD_VERIFICATION_CODES_BY_USER_ID_QUERY = """
                UPDATE reset_password_verifications
                SET deleted=true, modified_date=CURRENT_TIMESTAMP
                WHERE user_id=:userId
            """;

    public static final String INSERT_PASSWORD_VERIFICATION_CODE_QUERY = """
                INSERT INTO reset_password_verifications (user_id, url, expiration_date)
                VALUES (:userId, :url, :expirationDate)
            """;

    public static final String SELECT_USER_WITH_ROLE_BY_URL = """
            SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.address, u.phone, u.title, u.bio, u.image_url, u.enabled, u.locked, u.using_mfa, u.created_date, u.modified_date, r.name, r.permission, p.url, p.expiration_date AS url_expiration_date
            FROM users AS u JOIN roles AS r ON u.role_id=r.id JOIN reset_password_verifications AS p ON p.user_id=u.id
            WHERE p.url=:url AND p.deleted=false
        """;
}

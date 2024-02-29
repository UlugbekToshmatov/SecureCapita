package com.example.SecureCapitaInitializr.repositories.queries;

public class AccountVerificationQuery {
    public static String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = """
            INSERT INTO account_verifications (user_id, url)
            VALUES (:userId, :url)
        """;

    public static final String SELECT_BY_USER_ID_QUERY = """
            SELECT id, user_id, url FROM account_verifications
            WHERE user_id=:userId AND deleted=false
        """;

    public static final String SET_DELETED_TRUE_BY_USER_ID_QUERY = """
            UPDATE account_verifications SET deleted=true, modified_date=CURRENT_TIMESTAMP
            WHERE user_id=:userId AND deleted=false
        """;
}

package com.example.SecureCapitaInitializr.repositories.queries;

public class AccountVerificationQuery {
    public static String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = """
            INSERT INTO account_verifications (user_id, url)
            VALUES (:userId, :url)
        """;
}

package com.example.SecureCapitaInitializr.repositories.queries;

public class TwoFactorVerificationQuery {
    public static String DELETE_VERIFICATION_CODES_QUERY = """
                UPDATE two_factor_verifications
                SET deleted=true
                WHERE user_id=:userId
            """;

    public static String INSERT_VERIFICATION_CODE_QUERY = """
                INSERT INTO two_factor_verifications (user_id, code, expiration_date)
                VALUES (:userId, :code, :expirationDate)
            """;
}

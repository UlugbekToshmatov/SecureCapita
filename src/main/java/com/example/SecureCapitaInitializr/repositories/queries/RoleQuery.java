package com.example.SecureCapitaInitializr.repositories.queries;

public class RoleQuery {
    public static String FIND_ROLE_BY_ID_QUERY = "SELECT * FROM roles WHERE name=:name";
}

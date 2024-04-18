package com.example.SecureCapitaInitializr.repositories.queries;

public class RoleQuery {
    public static String SELECT_ALL_QUERY = "SELECT * FROM roles WHERE deleted=false ORDER BY id";
    public static String FIND_ROLE_BY_NAME_QUERY = "SELECT * FROM roles WHERE name=:name AND deleted=false";
    public static String FIND_ROLE_BY_USER_ID_QUERY = """
            SELECT r.id, r.name, r.permission
            FROM roles AS r JOIN users AS u ON r.id = u.role_id
            WHERE u.id=:userId AND u.deleted=false
        """;
    public static String UPDATE_ROLE_BY_USER_ID_QUERY = """
            UPDATE users
            SET role_id=:roleId
            WHERE id=:userid AND deleted=false
        """;
}

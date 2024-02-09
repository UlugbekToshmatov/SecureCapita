package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.role.Role;

public interface RoleRepository<T extends Role> {
    Role findByNameAndDeletedFalse(String name);
}

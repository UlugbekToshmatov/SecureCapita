package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.role.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {
    /* Basic CRUD Operations */
    T create(T data);
    Collection<T> list();
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    /* More Complex Operations */
    Role findByNameAndDeletedFalse(String name);
    Role getRoleByUserId(Long userId);
    void updateUserRole(Long userId, Integer roleId);

//    void addRoleToUser(Long userId, Long roleId);
}

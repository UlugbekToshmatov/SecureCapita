package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    /* Basic CRUD operations */
    T create(T user);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    /* More Complex Operations */
    Integer getEmailCount(String email);
}

package com.example.SecureCapitaInitializr.repositories;

import com.example.SecureCapitaInitializr.models.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

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
    UserDetails loadUserByUsername(String email);
    T findByEmailAndDeletedFalse(String email);
    void updatePasswordByUserId(Long userId, @NotEmpty(message = "New password cannot be empty") String newPassword);
    void activateUser(Long userId);
}

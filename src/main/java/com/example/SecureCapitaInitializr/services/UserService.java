package com.example.SecureCapitaInitializr.services;

import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;

public interface UserService {
    UserResponse create(UserRequest request);
}

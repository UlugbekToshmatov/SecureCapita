package com.example.SecureCapitaInitializr.services;

import com.example.SecureCapitaInitializr.dtos.role.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> findAll();
    RoleResponse findByUserId(Long userId);
    void updateByUserId(Long userId, String roleName);
}

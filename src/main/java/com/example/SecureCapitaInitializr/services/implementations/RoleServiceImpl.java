package com.example.SecureCapitaInitializr.services.implementations;

import com.example.SecureCapitaInitializr.dtos.role.RoleResponse;
import com.example.SecureCapitaInitializr.enums.RoleType;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.role.Role;
import com.example.SecureCapitaInitializr.models.user.User;
import com.example.SecureCapitaInitializr.repositories.RoleRepository;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import com.example.SecureCapitaInitializr.services.RoleService;
import com.example.SecureCapitaInitializr.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRepository;
    private final UserRepository<User> userRepository;


    @Override
    public List<RoleResponse> findAll() {
        return roleRepository.list().stream().map(role -> new RoleResponse(role.getName(), role.getPermission())).toList();
    }

    @Override
    public RoleResponse findByUserId(Long userId) {
        if (!userRepository.existsByUserId(userId))
            throw new ApiException("No user found with id="+ userId);

        Role role = roleRepository.getRoleByUserId(userId);
        return new RoleResponse(role.getName(), role.getPermission());
    }

    @Override
    public void updateByUserId(Long userId, String roleName) {
        if (roleName.equals(RoleType.ROLE_SYSADMIN.name()))
            throw new ApiException("Method not allowed");

        Role role = roleRepository.findByNameAndDeletedFalse(roleName);
        User currentUser = userRepository.get(UserUtils.getCurrentUserId());
        User userToBeUpdated = userRepository.get(userId);
        if (currentUser.getRoleId() <= userToBeUpdated.getRoleId() || currentUser.getRoleId() < role.getId())
            throw new ApiException("Method not allowed");

        roleRepository.updateUserRole(userId, role.getId());
    }
}

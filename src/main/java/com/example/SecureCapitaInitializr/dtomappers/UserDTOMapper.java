package com.example.SecureCapitaInitializr.dtomappers;

import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.models.role.Role;
import com.example.SecureCapitaInitializr.models.user.User;
import org.springframework.beans.BeanUtils;

public class UserDTOMapper {
    public static UserResponse mapToUserResponse(User user, Role role) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response, "password");
        response.setRole(role.getName());
        response.setPermission(role.getPermission());
        return response;
    }
}

package com.example.SecureCapitaInitializr.controllers;

import com.example.SecureCapitaInitializr.dtos.role.RoleResponse;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.example.SecureCapitaInitializr.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;



    @GetMapping
    public ResponseEntity<HttpResponse> getAllRoles() {
        List<RoleResponse> roles = roleService.findAll();
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .data(Map.of("roles", roles))
                .build()
        );
    }


    @PatchMapping("update/{user-id}/{role-name}")
    @PreAuthorize("hasAuthority('UPDATE:USER')")
    public ResponseEntity<HttpResponse> updateUserRole(@PathVariable("user-id") Long userId, @PathVariable("role-name") String roleName) {
        roleService.updateByUserId(userId, roleName);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Role updated successfully")
                .build()
        );
    }
}

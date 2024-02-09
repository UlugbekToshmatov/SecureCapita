package com.example.SecureCapitaInitializr.models.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserWithRole {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean enabled;
    private boolean locked;
    private boolean usingMfa;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String roleName;
    private String permission;
}

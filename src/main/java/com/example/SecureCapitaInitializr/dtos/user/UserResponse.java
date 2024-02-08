package com.example.SecureCapitaInitializr.dtos.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
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
    private String role;
    private String permission;
}

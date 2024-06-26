package com.example.SecureCapitaInitializr.models.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
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
    private Boolean enabled;
    private Boolean locked;
    private Boolean usingMfa;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Integer roleId;
}

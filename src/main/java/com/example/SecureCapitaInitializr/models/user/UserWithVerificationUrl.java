package com.example.SecureCapitaInitializr.models.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithVerificationUrl {
    private UserWithRole userWithRole;
    private String url;
    private LocalDateTime urlExpirationDate;
}

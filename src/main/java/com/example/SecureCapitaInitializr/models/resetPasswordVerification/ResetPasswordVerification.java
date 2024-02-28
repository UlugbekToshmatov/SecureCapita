package com.example.SecureCapitaInitializr.models.resetPasswordVerification;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResetPasswordVerification {
    private Long id;
    private Long userId;
    private String url;
    private LocalDateTime expirationDate;
}

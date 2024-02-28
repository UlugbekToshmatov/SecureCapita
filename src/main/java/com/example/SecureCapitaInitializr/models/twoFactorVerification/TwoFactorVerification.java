package com.example.SecureCapitaInitializr.models.twoFactorVerification;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TwoFactorVerification {
    private Long id;
    private Long userId;
    private String code;
    private LocalDateTime expirationDate;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

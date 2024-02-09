package com.example.SecureCapitaInitializr.models;

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
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

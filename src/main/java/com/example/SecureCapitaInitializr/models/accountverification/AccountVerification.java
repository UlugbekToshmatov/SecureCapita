package com.example.SecureCapitaInitializr.models.accountverification;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountVerification {
    private Long id;
    private Long userId;
    private String url;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

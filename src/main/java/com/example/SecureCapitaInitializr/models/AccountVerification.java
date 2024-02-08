package com.example.SecureCapitaInitializr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class AccountVerification {
    private Long id;
    private Long userId;
    private String url;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

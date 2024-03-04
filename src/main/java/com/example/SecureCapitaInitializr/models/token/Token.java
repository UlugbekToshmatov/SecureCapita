package com.example.SecureCapitaInitializr.models.token;

import com.example.SecureCapitaInitializr.enums.TokenType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Token {
    private Long id;
    private Long userId;
    private String token;
//    @Enumerated(EnumType.STRING)
    private TokenType type;
    private LocalDateTime expiresAt;
    private LocalDateTime issuedAt;
}

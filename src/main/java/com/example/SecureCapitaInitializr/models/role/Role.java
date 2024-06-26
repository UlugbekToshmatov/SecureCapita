package com.example.SecureCapitaInitializr.models.role;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role {
    private Integer id;
    private String name;
    private String permission;
}

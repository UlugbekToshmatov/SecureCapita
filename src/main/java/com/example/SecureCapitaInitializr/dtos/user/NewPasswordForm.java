package com.example.SecureCapitaInitializr.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordForm {
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "New password cannot be empty")
    private String confirmPassword;
}

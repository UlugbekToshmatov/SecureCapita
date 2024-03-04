package com.example.SecureCapitaInitializr.controllers;

import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.NewPasswordForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRegistrationForm;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.example.SecureCapitaInitializr.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid UserRegistrationForm request) {
        UserResponse userResponse = userService.create(request);
        return ResponseEntity.created(getUri()).body(
            HttpResponse.builder()
                .timeStamp(userResponse.getCreatedDate().toString())
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Registered successfully. Please, activate your account by following the verification link sent to your email.")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    @GetMapping("verify/account/{email}/{key}")
    public ResponseEntity<HttpResponse> activateAccount(@PathVariable("email") String email, @PathVariable("key") String key) {
        userService.verifyAccountKey(key, email);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Account activated successfully")
                .build()
        );
    }

    @PostMapping("login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm form, HttpServletRequest request) {
        UserResponse user = userService.login(form, request);
        boolean sendSms = user.isUsingMfa() && user.getPhone() != null;

        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message(sendSms? "Verification code sent to +" + user.getPhone(): "Login successful")
                .data(Map.of("user", user))
                .build()
        );
    }

    @GetMapping("verify/mfacode/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyMfaCode(
        @PathVariable("email") String email,
        @PathVariable("code") String code,
        HttpServletRequest request
    ) {
        UserResponse userResponse = userService.verifyMfaCode(email, code, request);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("User verified")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    @GetMapping("profile")
    public ResponseEntity<HttpResponse> getProfile(Authentication authentication) {
        UserResponse userResponse = userService.getByEmail(authentication.getName() /*authentication.getPrincipal()*/);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Profile retrieved")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    @GetMapping("refresh/token")
    public ResponseEntity<HttpResponse> getRefreshToken(HttpServletRequest request) {
        UserResponse userResponse = userService.refreshAccessToken(request);

        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Token refreshed")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    // Start - To reset password when user is not logged in

    @GetMapping("resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(String.valueOf(LocalDateTime.now()))
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Email sent. Please, check your email to reset your password.")
                .build()
        );
    }

    @GetMapping("verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyResetPasswordUrl(@PathVariable("key") String key) {
        UserResponse userResponse = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Please, enter a new password")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    @PutMapping("resetpassword/{key}")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@PathVariable("key") String key, @RequestBody NewPasswordForm form) {
        userService.updatePasswordWithKey(key, form);
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Password reset successfully")
                .build()
        );
    }

    // END - To reset password when user is not logged in

    @RequestMapping("error")
    public ResponseEntity<HttpResponse> error(HttpServletRequest request) {
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .reason("No mapping for a " + request.getMethod() + " request for this path found on the server")
                .build(), HttpStatus.NOT_FOUND
        );
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/<userId>").toUriString());
    }
}

package com.example.SecureCapitaInitializr.controllers;

import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.example.SecureCapitaInitializr.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm form) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getEmail().trim().toLowerCase(), form.getPassword()));
//        userService.login(form);
        UserResponse user = userService.getByEmail(form.getEmail());
        boolean sendSms = user.isUsingMfa() && user.getPhone() != null;
        if (sendSms) userService.sendVerificationCode(user);

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

    @PostMapping("register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid UserRequest request) {
        UserResponse userResponse = userService.create(request);
        return ResponseEntity.created(getUri()).body(
            HttpResponse.builder()
                .timeStamp(userResponse.getCreatedDate().toString())
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("User created")
                .data(Map.of("user", userResponse))
                .build()
        );
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/<userId>").toUriString());
    }
}

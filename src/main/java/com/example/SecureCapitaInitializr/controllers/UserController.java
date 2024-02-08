package com.example.SecureCapitaInitializr.controllers;

import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.example.SecureCapitaInitializr.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

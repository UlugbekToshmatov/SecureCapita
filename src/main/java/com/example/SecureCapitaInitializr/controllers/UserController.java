package com.example.SecureCapitaInitializr.controllers;

import com.example.SecureCapitaInitializr.dtomappers.UserDTOMapper;
import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.NewPasswordForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.jwtprovider.TokenProvider;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import com.example.SecureCapitaInitializr.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    private final TokenProvider tokenProvider;

    @PostMapping("login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm form) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getEmail().trim().toLowerCase(), form.getPassword()));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserResponse user = UserDTOMapper.mapToUserResponse(userPrincipal.getUser());
        boolean sendSms = user.isUsingMfa() && user.getPhone() != null;
        if (sendSms)
            userService.sendVerificationCode(user);
        else {
            user.setAccessToken(tokenProvider.createAccessToken(userPrincipal));
            user.setRefreshToken(tokenProvider.createRefreshToken(userPrincipal));
        }

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

    @GetMapping("verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserResponse userResponse = userService.verifyCode(email, code);
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

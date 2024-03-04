package com.example.SecureCapitaInitializr.services.implementations;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.SecureCapitaInitializr.dtomappers.UserDTOMapper;
import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.NewPasswordForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRegistrationForm;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.enums.VerificationType;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.jwtprovider.TokenProvider;
import com.example.SecureCapitaInitializr.models.resetPasswordVerification.ResetPasswordVerification;
import com.example.SecureCapitaInitializr.models.token.Token;
import com.example.SecureCapitaInitializr.models.twoFactorVerification.TwoFactorVerification;
import com.example.SecureCapitaInitializr.models.accountverification.AccountVerification;
import com.example.SecureCapitaInitializr.models.role.Role;
import com.example.SecureCapitaInitializr.models.user.User;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import com.example.SecureCapitaInitializr.models.user.UserWithRole;
import com.example.SecureCapitaInitializr.repositories.*;
import com.example.SecureCapitaInitializr.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.example.SecureCapitaInitializr.dtomappers.UserDTOMapper.mapToUserResponse;
import static com.example.SecureCapitaInitializr.enums.RoleType.ROLE_USER;
import static com.example.SecureCapitaInitializr.enums.VerificationType.ACCOUNT;
import static com.example.SecureCapitaInitializr.enums.VerificationType.PASSWORD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    private final AccountVerificationRepository<AccountVerification> accountVerificationRepository;
    private final TwoFactorVerificationRepository<TwoFactorVerification> twoFactorVerificationRepository;
    private final ResetPasswordVerificationRepository<ResetPasswordVerification> resetPasswordVerificationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenRepository<Token> tokenRepository;
    private final AuthenticationManager authenticationManager;


    // START - To register with account verification
    @Override
    @Transactional
    public UserResponse create(UserRegistrationForm request) {
        log.info("Registering user-{} with email: '{}'", request.getFirstName(), request.getEmail());
        if (userRepository.getEmailCount(request.getEmail().trim().toLowerCase()) > 0)
            throw new ApiException("Email already in use. Please, use a different email and try again.");

        Role role = roleRepository.findByNameAndDeletedFalse(request.getRole());
        if (role == null)
            throw new ApiException("No role found by name: " + ROLE_USER.name());

        try {
            User createdUser = userRepository.create(buildUser(request, role.getId()));
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            accountVerificationRepository.saveActivationLink(createdUser.getId(), verificationUrl);
            sendEmail(createdUser.getFirstName(), createdUser.getEmail(), verificationUrl, ACCOUNT);
            return mapToUserResponse(createdUser, role);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please, try again.");
        }
    }

    @Override
    @Transactional
    public void verifyAccountKey(String key, String email) {
        // get user by email
        User user = userRepository.findByEmailAndDeletedFalse(email.trim().toLowerCase());

        // get account verification by userId
        AccountVerification accountVerification = accountVerificationRepository.getAccountVerificationByUserId(user.getId());

        // if the url from account verification matches the one from request, then set user as activated
        if (!accountVerification.getUrl().equals(getVerificationUrl(key, ACCOUNT.getType())))
            throw new ApiException("Invalid key!");

        userRepository.activateUser(user.getId());
        accountVerificationRepository.deleteAccountVerificationUrlByUserId(user.getId());
    }
    // END - To register with account verification

    // START - To log in with two-factor authentication if mfa is enabled
    @Override
    public UserResponse login(LoginForm form, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getEmail().trim().toLowerCase(), form.getPassword()));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserResponse user = UserDTOMapper.mapToUserResponse(userPrincipal.getUser());
        boolean sendSms = user.isUsingMfa() && user.getPhone() != null;
        if (sendSms)
            sendMfaVerificationCode(user);
        else {
            setTokens(userPrincipal, user, request);
        }
        return user;
    }

    @Override
    @Transactional
    public void sendMfaVerificationCode(UserResponse user) {
        LocalDateTime expirationDate = getExpirationDate();
        String verificationCode = RandomStringUtils.randomAlphanumeric(8);
        log.info("Verification code: {}", verificationCode);
        final String message = "From SecureCapita\n\nVerification code: " + verificationCode;
        try {
            twoFactorVerificationRepository.deleteVerificationCodesByUserId(user.getId());
            twoFactorVerificationRepository.insertVerificationCode(
                user.getId(),
                verificationCode,
                expirationDate
            );
            // Sends SMS to user if you have Twilio account
//            SmsUtils.sendSms(userResponse.getPhone(), message);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please, try again.");
        }
    }

    @Override
    public UserResponse verifyMfaCode(String email, String code, HttpServletRequest request) {
        // get user by email
        UserPrincipal userPrincipal = (UserPrincipal) userRepository.loadUserByUsername(email.trim().toLowerCase());

        // get id of the user and get the generated code from two factor verification repo with it
        TwoFactorVerification twoFactorVerification =
            twoFactorVerificationRepository.getCodeByUserId(userPrincipal.getUser().getId());

        // check if the code is not expired
        if (twoFactorVerification.getExpirationDate().isBefore(LocalDateTime.now()))
            throw new ApiException("The code has expired. Please, login again.");

        // if the two codes match, return response with tokens
        if (twoFactorVerification.getCode().equals(code)) {
            UserResponse userResponse = mapToUserResponse(userPrincipal.getUser());
            setTokens(userPrincipal, userResponse, request);
            // invalidate the code sent back from email by user at the end for data integrity purposes in case of an error
            twoFactorVerificationRepository.deleteVerificationCodesByUserId(userPrincipal.getUser().getId());
            return userResponse;
        } else throw new ApiException("Invalid code provided");
    }
    // END - To log in with two-factor authentication if mfa is enabled

    // START - To reset password when user is not logged in
    @Override
    @Transactional
    public void resetPassword(String email) {
        // Working without try-catch blocks as exception handlers handle any exceptions
        User user = userRepository.findByEmailAndDeletedFalse(email.trim().toLowerCase());
        if (user == null)
            throw new ApiException("No user found with email '"+ email + "'");

        LocalDateTime expirationDate = getExpirationDate();
        String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
        resetPasswordVerificationRepository.deletePasswordVerificationCodesByUserId(user.getId());
        resetPasswordVerificationRepository.insertPasswordVerificationCode(user.getId(), verificationUrl, expirationDate);
        // send verification URL to email
        log.info("Verification url '{}' sent to email address '{}'", verificationUrl, email);
    }

    @Override
    public UserResponse verifyPasswordKey(String key) {
        UserWithRole user = resetPasswordVerificationRepository.verifyPasswordKey(getVerificationUrl(key, PASSWORD.getType()));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void updatePasswordWithKey(String key, NewPasswordForm form) {
        ResetPasswordVerification verification = resetPasswordVerificationRepository.getVerificationByUrl(getVerificationUrl(key, PASSWORD.getType()));
        if (verification == null)
            throw new ApiException("Invalid key");
        if (verification.getExpirationDate().isBefore(LocalDateTime.now()))
            throw new ApiException("This link has expired. Please, reset your password again.");

        if (!form.getPassword().equals(form.getConfirmPassword()))
            throw new ApiException("Passwords do not match. Please, try again.");

        userRepository.updatePasswordByUserId(verification.getUserId(), passwordEncoder.encode(form.getConfirmPassword()));
        resetPasswordVerificationRepository.deletePasswordVerificationCodesByUserId(verification.getUserId());
    }
    // END - To reset password when user is not logged in

    @Override
    public UserResponse getByEmail(String email) {
        UserPrincipal userPrincipal = (UserPrincipal) userRepository.loadUserByUsername(email.trim().toLowerCase());
        return mapToUserResponse(userPrincipal.getUser());
    }

    @Override
    @Transactional
    public UserResponse refreshAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new ApiException("Missing or Invalid token");
        String jwt = authHeader.substring("Bearer ".length());
        try {
            // getSubject() validates both jwt itself and its expiration
            String email = tokenProvider.getSubject(jwt, request);

            // If code has reached here, then jwt is a valid and not expired token.
            // Otherwise, getSubject() above would have thrown an exception.
            Token token = tokenRepository.getByToken(jwt);
            UserPrincipal userPrincipal = (UserPrincipal) userRepository.loadUserByUsername(email);
            if (Objects.equals(userPrincipal.getUser().getId(), token.getUserId())) {
                UserResponse userResponse = UserDTOMapper.mapToUserResponse(userPrincipal.getUser());
                String accessToken = tokenProvider.createAccessToken(userPrincipal);
                tokenRepository.revokeAccessTokensByUserId(userResponse.getId());
                tokenRepository.insertToken(userResponse.getId(), accessToken, tokenProvider.getExpiration(accessToken, request));
                userResponse.setAccessToken(accessToken);
                userResponse.setRefreshToken(jwt);
                return userResponse;
            } else {
                tokenRepository.revokeToken(jwt);
                throw new ApiException("Sorry, an error occurred?! Please, log in again.");
            }
        } catch (TokenExpiredException exception) {
            tokenRepository.expireToken(jwt);
            throw new ApiException("Refresh token expired. Please, log in again.");
        }
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/verify/" + type + "/" + key).toUriString();
    }

    private LocalDateTime getExpirationDate() {
        Date date = DateUtils.addDays(new Date(), 1);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        log.info("Sending code '{}' to email address '{}'...", verificationUrl, email);
    }

    private User buildUser(UserRegistrationForm request, Integer roleId) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(roleId);
        if (request.getUsingMfa() != null)
            user.setUsingMfa(request.getUsingMfa());
        return user;
    }

    private void setTokens(UserPrincipal userPrincipal, UserResponse userResponse, HttpServletRequest request) {
        String accessToken = tokenProvider.createAccessToken(userPrincipal);
        String refreshToken = tokenProvider.createRefreshToken(userPrincipal);
        tokenRepository.revokeAllTokensByUserId(userResponse.getId());
        tokenRepository.insertTokens(
            userResponse.getId(),
            accessToken,
            refreshToken,
            tokenProvider.getExpiration(accessToken, request),
            tokenProvider.getExpiration(refreshToken, request)
        );
        userResponse.setAccessToken(accessToken);
        userResponse.setRefreshToken(refreshToken);
    }
}

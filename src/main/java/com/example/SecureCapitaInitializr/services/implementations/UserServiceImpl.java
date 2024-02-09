package com.example.SecureCapitaInitializr.services.implementations;

import com.example.SecureCapitaInitializr.dtos.user.LoginForm;
import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.enums.VerificationType;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.TwoFactorVerification;
import com.example.SecureCapitaInitializr.models.accountverification.AccountVerification;
import com.example.SecureCapitaInitializr.models.role.Role;
import com.example.SecureCapitaInitializr.models.user.User;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import com.example.SecureCapitaInitializr.repositories.AccountVerificationRepository;
import com.example.SecureCapitaInitializr.repositories.RoleRepository;
import com.example.SecureCapitaInitializr.repositories.TwoFactorVerificationRepository;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import com.example.SecureCapitaInitializr.services.UserService;
import com.example.SecureCapitaInitializr.utils.SmsUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static com.example.SecureCapitaInitializr.dtomappers.UserDTOMapper.mapToUserResponse;
import static com.example.SecureCapitaInitializr.enums.RoleType.ROLE_USER;
import static com.example.SecureCapitaInitializr.enums.VerificationType.ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    private final AccountVerificationRepository<AccountVerification> accountVerificationRepository;
    private final TwoFactorVerificationRepository<TwoFactorVerification> twoFactorVerificationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
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
    public void login(LoginForm form) {
        User user = userRepository.findByEmailAndDeletedFalse(form.getEmail().trim().toLowerCase());
        if (!passwordEncoder.matches(form.getPassword(), user.getPassword()))
            throw new ApiException("Incorrect password. Please, try again.");
    }

    @Override
    public UserResponse getByEmail(String email) {
        UserPrincipal userPrincipal = (UserPrincipal) userRepository.loadUserByUsername(email.trim().toLowerCase());
        return mapToUserResponse(userPrincipal.getUser());
    }

    @Override
    @Transactional
    public String sendVerificationCode(UserResponse user) {
        LocalDateTime expirationDate = LocalDateTime.of(
            LocalDateTime.now().getYear(),
            LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth() + 1,
            LocalDateTime.now().getHour(),
            LocalDateTime.now().getMinute(),
            LocalDateTime.now().getSecond()
        );
//        String expirationDate = DateFormatUtils.format(DateUtils.addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = RandomStringUtils.randomAlphanumeric(8);
        final String message = "From SecureCapita\n\nVerification code: " + verificationCode;
        try {
            twoFactorVerificationRepository.deleteVerificationCodesByUserId(user.getId());
            twoFactorVerificationRepository.insertVerificationCode(user.getId(), verificationCode, expirationDate);
            // Sends SMS to user if Twilio account is present
            SmsUtils.sendSms(user.getPhone(), message);
        } catch (Exception exception) {
            log.error(exception.getMessage() + ", line 105");
            throw new ApiException("An error occurred. Please, try again.");
        }

        return null;
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/verify/" + type + "/" + key).toUriString();
    }

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        log.info("Sending email to address {}...", email);
    }

    private User buildUser(UserRequest request, Integer roleId) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(roleId);
        return user;
    }
}

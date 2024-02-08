package com.example.SecureCapitaInitializr.services.implementations;

import com.example.SecureCapitaInitializr.dtos.user.UserRequest;
import com.example.SecureCapitaInitializr.dtos.user.UserResponse;
import com.example.SecureCapitaInitializr.enums.VerificationType;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.AccountVerification;
import com.example.SecureCapitaInitializr.models.Role;
import com.example.SecureCapitaInitializr.models.User;
import com.example.SecureCapitaInitializr.repositories.AccountVerificationRepository;
import com.example.SecureCapitaInitializr.repositories.RoleRepository;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import com.example.SecureCapitaInitializr.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static com.example.SecureCapitaInitializr.dtomappers.UserDTOMapper.mapToUserResponse;
import static com.example.SecureCapitaInitializr.enums.RoleType.ROLE_USER;
import static com.example.SecureCapitaInitializr.enums.VerificationType.ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    private final AccountVerificationRepository<AccountVerification> accountVerificationRepository;
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

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/verify/" + type + "/" + key).toUriString();
    }

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        log.info("Sending email to address {}...", email);
    }

    private User buildUser(UserRequest request, Integer roleId) {
//        User user = User.builder()
//            .firstName(request.getFirstName())
//            .lastName(request.getLastName())
//            .email(request.getEmail())
//            .password(request.getPassword())
//            .address(request.getAddress())
//            .password(request.getPhone())
//            .title(request.getTitle())
//            .bio(request.getBio())
//            .roleId(roleId)
//            .build();
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(roleId);
        return user;
    }
}

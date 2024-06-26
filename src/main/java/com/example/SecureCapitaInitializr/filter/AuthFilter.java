package com.example.SecureCapitaInitializr.filter;

import com.example.SecureCapitaInitializr.jwtprovider.TokenProvider;
import com.example.SecureCapitaInitializr.models.token.Token;
import com.example.SecureCapitaInitializr.models.user.User;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import com.example.SecureCapitaInitializr.repositories.TokenRepository;
import com.example.SecureCapitaInitializr.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.SecureCapitaInitializr.utils.ExceptionUtils.processError;
import static java.util.Optional.ofNullable;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    // PUBLIC_ROUTES must have exact routes unlike the ones in SecurityConfig
    private static final String[] PUBLIC_ROUTES = {
        "/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/verify/mfacode", "/api/v1/user/resetpassword",
        "/api/v1/user/refresh/token"
    };
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private final TokenProvider tokenProvider;
    private final UserRepository<User> userRepository;
    private final TokenRepository<Token> tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        try {
            String token = getToken(request);
            Long userId = tokenProvider.getSubject(token, request);
            log.info("Validating user with id={} in Filter", userId);
            if (tokenProvider.isTokenValid(userId, token) && tokenRepository.isTokenNotRevoked(token)) {
                // pass UserPrincipal to Authentication to access any data of the requesting user anywhere
                UserPrincipal userPrincipal = (UserPrincipal) userRepository.getUserById(userId);
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userPrincipal, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else { SecurityContextHolder.clearContext(); }
            filter.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            processError(response, exception);
        }
    }

    // The following method is to make code cleaner (optional) by writing if statements here instead of inside doFilterInternal.
    // If this method of OncePerRequestFilter class always returns true, any incoming request is NOT filtered (as shouldNotFilter: true).
    // This method is always called first before doFilterInternal, based on the result of which requests are either filtered or not.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
            request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) || Arrays.stream(PUBLIC_ROUTES).anyMatch(route -> request.getRequestURI().contains(route));
        log.info("Should Not Filter???: {}", shouldNotFilter);
        return shouldNotFilter;
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
            .filter(header -> header.startsWith(TOKEN_PREFIX))
            .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
            .orElseThrow();
    }
}

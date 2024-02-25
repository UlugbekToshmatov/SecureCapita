package com.example.SecureCapitaInitializr.filter;

import com.example.SecureCapitaInitializr.jwtprovider.TokenProvider;
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
import java.util.Map;

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
        "/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/verify/code"
    };
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private final TokenProvider tokenProvider;
    private final String TOKEN_KEY = "token";
    private final String EMAIL_KEY = "email";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        try {
            Map<String, String> headers = getRequestHeaders(request);
            String token = getToken(request);
            if (tokenProvider.isTokenValid(headers.get(EMAIL_KEY), token)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(headers.get(EMAIL_KEY), authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else { SecurityContextHolder.clearContext(); }
            filter.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
//            processError(request, response, exception);
        }
    }

    // The following method is to make code cleaner (optional) by writing if statements here instead of inside doFilterInternal.
    // If this method of OncePerRequestFilter class always returns true, any incoming request is NOT filtered (as shouldNotFilter: true).
    // This method is always called first before doFilterInternal, based on the result of which requests are either filtered or not.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
            request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) || Arrays.asList(PUBLIC_ROUTES).contains(request.getRequestURI());
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        return Map.of(
            EMAIL_KEY, tokenProvider.getSubject(getToken(request), request),
            TOKEN_KEY, getToken(request)
        );
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
            .filter(header -> header.startsWith(TOKEN_PREFIX))
            .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
            .orElseThrow();
    }
}

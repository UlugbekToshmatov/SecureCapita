package com.example.SecureCapitaInitializr.jwtprovider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.SecureCapitaInitializr.models.user.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private static final String GET_ARRAYS_LLC = "GET ARRAYS LLC";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER MANAGEMENT SERVICE";
    public static final String AUTHORITIES = "authorities";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;   // 1 800 000 millis = 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;  // 432 000 000 millis = 5 days
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    @Value("${jwt.secret}")
    private String secret;

    public String createAccessToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(GET_ARRAYS_LLC).withAudience(CUSTOMER_MANAGEMENT_SERVICE)
            .withIssuedAt(new Date()).withSubject(String.valueOf(userPrincipal.getUser().getId())).withArrayClaim(AUTHORITIES, claims)
            .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserPrincipal userPrincipal) {
        return JWT.create().withIssuer(GET_ARRAYS_LLC).withAudience(CUSTOMER_MANAGEMENT_SERVICE)
            .withIssuedAt(new Date()).withSubject(String.valueOf(userPrincipal.getUser().getId()))
            .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public Long getSubject(String token, HttpServletRequest request) {
        Long subject;
        try {
            subject = Long.valueOf(getJwtVerifier().verify(token).getSubject());
        } catch (TokenExpiredException exception) {
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (JWTVerificationException exception) {
            request.setAttribute("tokenNotVerified", exception.getMessage());
            throw exception;
        }
        return subject;
    }

    public Date getExpiration(String token, HttpServletRequest request) {
        try {
            return getJwtVerifier().verify(token).getExpiresAt();
        } catch (TokenExpiredException exception) {
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (JWTVerificationException exception) {
            request.setAttribute("tokenNotVerified", exception.getMessage());
            throw exception;
        }
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(UserDetails user, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthToken;
    }

    // If token was expired/invalid, getSubject() would return null, which would cause getRequestHeaders() throw NullPointerException.
    // So, if this method is reached, then getSubject() must have returned a valid id from a valid unexpired token.
    public boolean isTokenValid(Long userId, String token) {
        JWTVerifier verifier = getJwtVerifier();

        // Check if userId is not null
        if (Objects.isNull(userId))
            return false;

        // Since this method is reached and getSubject() returned a valid id from a valid token, this check is unnecessary
        return !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date(/*System.currentTimeMillis()*/));
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return jwtVerifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJwtVerifier() {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            return JWT.require(algorithm).withIssuer(GET_ARRAYS_LLC).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        // getAuthorities() returns Collection<GrantedAuthority>
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }
}

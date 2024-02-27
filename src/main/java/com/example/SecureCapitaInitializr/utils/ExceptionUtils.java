package com.example.SecureCapitaInitializr.utils;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.SecureCapitaInitializr.exceptions.ApiException;
import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class ExceptionUtils {
    public static void processError(HttpServletResponse response, Exception exception) {
        HttpResponse httpResponse;
        if (exception instanceof ApiException || exception instanceof DisabledException || exception instanceof LockedException ||
            exception instanceof TokenExpiredException || exception instanceof InvalidClaimException ||
            exception instanceof JWTDecodeException || exception instanceof BadCredentialsException) {
            httpResponse = getHttpResponse(response, exception.getMessage(), BAD_REQUEST);
        } else if (exception instanceof JWTVerificationException) {
            httpResponse = getHttpResponse(response, "Token not verified", BAD_REQUEST);
        } else {
            httpResponse = getHttpResponse(response, "An error occurred. Please, try again.", INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, httpResponse);
        log.error(exception.getMessage());
    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
        try {
            // ServletOutputStream is returned back to clients inside HttpServletResponse with data in it
            ServletOutputStream outputStream = response.getOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            // Converting HttpResponse to Json, and writing it to ServletOutputStream
            objectMapper.writeValue(outputStream, httpResponse);
            outputStream.flush();
            outputStream.close();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static HttpResponse getHttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus) {
        HttpResponse httpResponse = HttpResponse.builder()
            .timeStamp(LocalDateTime.now().toString())
            .statusCode(httpStatus.value())
            .status(httpStatus)
            .reason(message)
            .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());

        return httpResponse;
    }
}

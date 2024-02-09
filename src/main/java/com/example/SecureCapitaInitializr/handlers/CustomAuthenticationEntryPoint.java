package com.example.SecureCapitaInitializr.handlers;

import com.example.SecureCapitaInitializr.models.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
            .timeStamp(LocalDateTime.now().toString())
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .status(HttpStatus.UNAUTHORIZED)
            .reason("You need to log in to access resources")
            .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // ServletOutputStream is returned back to clients inside HttpServletResponse with data in it
        ServletOutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        // Converting HttpResponse to Json, and writing it to ServletOutputStream
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        outputStream.close();
    }
}

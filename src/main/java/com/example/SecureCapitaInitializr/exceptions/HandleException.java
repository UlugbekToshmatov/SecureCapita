package com.example.SecureCapitaInitializr.exceptions;

import com.example.SecureCapitaInitializr.models.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


// Implementing ErrorController is necessary for handling 404 not found error
@RestControllerAdvice
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())
                .status(HttpStatus.resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .build(), statusCode
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(fieldMessage)
                .developerMessage(exception.getMessage())
                .status(HttpStatus.resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .build(), statusCode
        );
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage().contains("Duplicate entry")? "Information already exists": exception.getMessage())
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage() + ", incorrect username or password")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledException(DisabledException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage() + ". Please, check your email and verify your account.")
//                .reason("User account currently disabled")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("User account currently locked")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("Access denied. You do not have permission.")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .build(), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("You need to log in to access resources")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage().contains("expected 1, actual 0")? "Record not found": exception.getMessage())
                .developerMessage(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
            HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(exception.getMessage() != null ? exception.getMessage(): "An error occurred")
                .developerMessage(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

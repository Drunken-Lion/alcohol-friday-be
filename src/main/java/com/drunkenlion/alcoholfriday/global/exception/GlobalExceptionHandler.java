package com.drunkenlion.alcoholfriday.global.exception;

import com.drunkenlion.alcoholfriday.global.common.response.ErrorResponse;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Custom Exception Handler
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handlerBusinessException(BusinessException e, HttpServletRequest request) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.of(
                        e.getMessage(),
                        request
                ));
    }

    /**
     * Validation 관련 Exception Handler
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            UnexpectedTypeException.class
    })
    public ResponseEntity<ErrorResponse> handleBindException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpResponse.Fail.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.of(
                        HttpResponse.Fail.INVALID_INPUT_VALUE.getMessage(),
                        request
                ));
    }

    /**
     * HTTP 관련 Exception Handler
     */
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleHttpException(Exception e, HttpServletRequest request) {
        HttpResponse.Fail response;

        String exceptionName = e.getClass().getSimpleName();
        switch (exceptionName) {
            case "HttpRequestMethodNotSupportedException" -> response = HttpResponse.Fail.METHOD_NOT_ALLOWED;
            case "AccessDeniedException" -> response = HttpResponse.Fail.DEACTIVATE_USER;
            case "MissingServletRequestParameterException" -> response = HttpResponse.Fail.MISSING_PARAMETER;
            default -> response = HttpResponse.Fail.BAD_REQUEST;
        }

        return ResponseEntity
                .status(response.getStatus())
                .body(ErrorResponse.of(
                        response.getMessage(),
                        request
                ));
    }

    /**
     * Security 관련 Exception Handler
     */
    @ExceptionHandler({
            AccountExpiredException.class,
            AccountStatusException.class,
            AuthenticationCredentialsNotFoundException.class,
            AuthenticationServiceException.class,
            BadCredentialsException.class,
            CredentialsExpiredException.class,
            OAuth2AuthenticationException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleSecurityException(Exception e, HttpServletRequest request) {
        HttpResponse.Fail response;

        String exceptionName = e.getClass().getSimpleName();
        switch (exceptionName) {
            case "AuthenticationCredentialsNotFoundException", "BadCredentialsException" ->
                    response = HttpResponse.Fail.INVALID_TOKEN;
            case "AccountExpiredException", "CredentialsExpiredException" -> response = HttpResponse.Fail.EXPIRED_TOKEN;
            case "AccountStatusException", "OAuth2AuthenticationException", "UsernameNotFoundException" ->
                    response = HttpResponse.Fail.INVALID_ACCOUNT;
            default -> response = HttpResponse.Fail.UNAUTHORIZED;
        }

        return ResponseEntity
                .status(response.getStatus())
                .body(ErrorResponse.of(
                        response.getMessage(),
                        request
                ));
    }

    /**
     * 설정하지 않은 Exception 처리 Handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerAllException(Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpResponse.Fail.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(
                        HttpResponse.Fail.INTERNAL_SERVER_ERROR.getMessage(),
                        request,
                        e
                ));
    }
}

package com.drunkenlion.alcoholfriday.global.exception;

import com.drunkenlion.alcoholfriday.global.common.response.Code;
import com.drunkenlion.alcoholfriday.global.common.response.ErrorResponse;
import com.drunkenlion.alcoholfriday.global.common.response.Message;
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
                        e.getStatus(),
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
                .status(Code.Fail.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.of(
                        Code.Fail.INVALID_INPUT_VALUE.getStatus(),
                        Message.Fail.INVALID_INPUT_VALUE.getMessage(),
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
            AccessDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleHttpException(Exception e, HttpServletRequest request) {
        Code.Fail code;
        Message.Fail message;

        String exceptionName = e.getClass().getSimpleName();
        switch (exceptionName) {
            case "HttpRequestMethodNotSupportedException" -> {
                code = Code.Fail.METHOD_NOT_ALLOWED;
                message = Message.Fail.METHOD_NOT_ALLOWED;
            }
            case "AccessDeniedException" -> {
                code = Code.Fail.DEACTIVATE_USER;
                message = Message.Fail.DEACTIVATE_USER;
            }
            default -> {
                code = Code.Fail.BAD_REQUEST;
                message = Message.Fail.BAD_REQUEST;
            }
        }

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(
                        code.getStatus(),
                        message.getMessage(),
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
        Code.Fail code;
        Message.Fail message;

        String exceptionName = e.getClass().getSimpleName();
        switch (exceptionName) {
            case "AuthenticationCredentialsNotFoundException", "BadCredentialsException" -> {
                code = Code.Fail.INVALID_TOKEN;
                message = Message.Fail.INVALID_TOKEN;
            }
            case "AccountExpiredException", "CredentialsExpiredException" -> {
                code = Code.Fail.EXPIRED_TOKEN;
                message = Message.Fail.EXPIRED_TOKEN;
            }
            case "AccountStatusException", "OAuth2AuthenticationException", "UsernameNotFoundException" -> {
                code = Code.Fail.INVALID_ACCOUNT;
                message = Message.Fail.INVALID_ACCOUNT;
            }
            default -> {
                code = Code.Fail.UNAUTHORIZED;
                message = Message.Fail.UNAUTHORIZED;
            }
        }

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(
                        code.getStatus(),
                        message.getMessage(),
                        request
                ));
    }

    /**
     * 설정하지 않은 Exception 처리 Handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerAllException(Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(Code.Fail.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(
                        Code.Fail.INTERNAL_SERVER_ERROR.getStatus(),
                        Message.Fail.INTERNAL_SERVER_ERROR.getMessage(),
                        request
                ));
    }
}

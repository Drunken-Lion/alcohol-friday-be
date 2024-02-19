package com.drunkenlion.alcoholfriday.global.common.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ErrorResponse {
    private String httpMethod;
    private String path;
    private String message;
    private LocalDateTime timestamp;
    private Exception error;

    public static ErrorResponse of(final String message, final HttpServletRequest request) {
        return ErrorResponse.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(final String message, final HttpServletRequest request, Exception error) {
        return ErrorResponse.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

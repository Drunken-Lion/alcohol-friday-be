package com.drunkenlion.alcoholfriday.global.common.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.http.HttpStatus;

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

    public static ErrorResponse of(final String message, final HttpServletRequest request) {
        return ErrorResponse.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

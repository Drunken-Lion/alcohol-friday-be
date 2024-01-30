package com.drunkenlion.alcoholfriday.global.common.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ErrorResponse {
    private int status;
    private String httpMethod;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public static ErrorResponse of(final int code, final String message, final HttpServletRequest request) {
        return ErrorResponse.builder()
                .status(code)
                .httpMethod(request.getMethod())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

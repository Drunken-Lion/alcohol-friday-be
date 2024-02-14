package com.drunkenlion.alcoholfriday.global.security.jwt.enumerated;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS_TOKEN("access-token"),
    REFRESH_TOKEN("refresh-token");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }
}

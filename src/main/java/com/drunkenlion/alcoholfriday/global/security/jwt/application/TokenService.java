package com.drunkenlion.alcoholfriday.global.security.jwt.application;

import com.drunkenlion.alcoholfriday.global.security.jwt.entity.RefreshToken;
import org.springframework.security.core.Authentication;

import java.util.Date;

public interface TokenService {
    RefreshToken createRefreshToken(String token, Date expiryDate, Authentication authentication);

    RefreshToken findRefreshToken(String refreshToken);

    void deleteRefreshToken(String email);
}

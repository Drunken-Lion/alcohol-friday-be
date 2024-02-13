package com.drunkenlion.alcoholfriday.domain.auth.application;

import com.drunkenlion.alcoholfriday.domain.auth.dto.LoginResponse;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;

public interface AuthService {
    LoginResponse testLogin(String username);

    LoginResponse socialLogin(ProviderType provider, String accessToken);

    JwtResponse reissueToken(String requestRefreshToken);
}

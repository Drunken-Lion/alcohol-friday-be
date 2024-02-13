package com.drunkenlion.alcoholfriday.domain.auth.application;

import com.drunkenlion.alcoholfriday.domain.auth.dto.LoginResponse;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;

public interface AuthService {
	LoginResponse testLogin(String username);

	LoginResponse socialLogin(ProviderType provider, String accessToken);

	LoginResponse reissueToken(String requestRefreshToken);
}

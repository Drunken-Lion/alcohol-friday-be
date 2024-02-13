package com.drunkenlion.alcoholfriday.domain.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drunkenlion.alcoholfriday.domain.auth.application.AuthService;
import com.drunkenlion.alcoholfriday.domain.auth.dto.LoginResponse;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "v1-auth-controller", description = "회원 인증을 담당하는 컨트롤러")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/test")
	public ResponseEntity<LoginResponse> testLogin(@RequestBody String username) {
		LoginResponse loginResponse = authService.testLogin(username);
		return ResponseEntity.ok().body(loginResponse);
	}

	@Operation(summary = "소셜 로그인")
	@PostMapping("/login/{provider}")
	public ResponseEntity<LoginResponse> login(@PathVariable ProviderType provider,
		@RequestBody String providerAccessToken) {
		if (providerAccessToken == null) {
			throw new BusinessException(HttpResponse.Fail.BAD_REQUEST);
		}

		LoginResponse loginResponse = authService.socialLogin(provider, providerAccessToken);
		return ResponseEntity.ok().body(loginResponse);
	}

	@Operation(summary = "액세스 토큰 재발급")
	@PostMapping("/reissue-token")
	public ResponseEntity<JwtResponse> reissueToken(@RequestBody String refreshToken) {
		if (refreshToken == null) {
			throw new BusinessException(HttpResponse.Fail.BAD_REQUEST);
		}

		JwtResponse jwtResponse = authService.reissueToken(refreshToken);
		return ResponseEntity.ok().body(jwtResponse);
	}
}

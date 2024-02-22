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

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "v1-auth", description = "회원 인증을 담당하는 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/test")
    public ResponseEntity<LoginResponse> testLogin(@RequestBody String email) {
        LoginResponse loginResponse = authService.testLogin(email);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "소셜 로그인", description = "제공처의 액세스 토큰으로 회원 정보 조회, 로그인 처리 후 액세스 토큰과 회원 정보를 응답으로 전송")
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(@PathVariable ProviderType provider,
                                               @RequestBody String providerAccessToken) {
        validRequestToken(providerAccessToken);

        LoginResponse loginResponse = authService.socialLogin(provider, providerAccessToken);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰이 유효하다면, 새로운 액세스 토큰 발급")
    @PostMapping("/reissue-token")
    public ResponseEntity<JwtResponse> reissueToken(@RequestBody String refreshToken) {
        validRequestToken(refreshToken);

        JwtResponse jwtResponse = authService.reissueToken(refreshToken);
        return ResponseEntity.ok().body(jwtResponse);
    }

    private void validRequestToken(String token) {
        Optional.ofNullable(token)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.BAD_REQUEST));
    }
}

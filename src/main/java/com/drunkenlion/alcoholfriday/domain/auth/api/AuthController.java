package com.drunkenlion.alcoholfriday.domain.auth.api;

import com.drunkenlion.alcoholfriday.domain.auth.application.AuthService;
import com.drunkenlion.alcoholfriday.domain.auth.dto.LoginResponse;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.auth.util.AuthValidator;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "v1-auth", description = "회원 인증을 담당하는 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "테스트 로그인", description = "임의의 아이디로 로그인")
    @PostMapping(value = "test", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<LoginResponse> testLogin(@RequestBody String email) {
        LoginResponse loginResponse = authService.testLogin(email);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "소셜 로그인", description = "제공처의 액세스 토큰으로 회원 정보 조회, 로그인 처리 후 액세스 토큰과 회원 정보를 응답으로 전송")
    @PostMapping(value = "login/{provider}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<LoginResponse> login(@PathVariable("provider") ProviderType provider,
                                               @RequestBody String providerAccessToken) {
        AuthValidator.validRequestToken(providerAccessToken);

        LoginResponse loginResponse = authService.socialLogin(provider, providerAccessToken);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰이 유효하다면, 새로운 액세스 토큰 발급")
    @PostMapping(value = "reissue-token", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<JwtResponse> reissueToken(@RequestBody String refreshToken) {
        AuthValidator.validRequestToken(refreshToken);

        JwtResponse jwtResponse = authService.reissueToken(refreshToken);
        return ResponseEntity.ok().body(jwtResponse);
    }

    @Operation(summary = "성인 인증")
    @PostMapping(value = "certifications", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> certifyAdult(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestBody String impUid) {
        AuthValidator.validateCertifyAt(userPrincipal.getMember());

        authService.authenticateAdultUser(userPrincipal.getMember(), impUid);

        return ResponseEntity.noContent().build();
    }
}

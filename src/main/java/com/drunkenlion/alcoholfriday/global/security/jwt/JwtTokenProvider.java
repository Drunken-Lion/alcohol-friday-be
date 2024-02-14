package com.drunkenlion.alcoholfriday.global.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.drunkenlion.alcoholfriday.global.security.jwt.application.TokenServiceImpl;
import com.drunkenlion.alcoholfriday.global.security.jwt.enumerated.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.drunkenlion.alcoholfriday.global.security.auth.UserDetailsServiceImpl;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.access-token-secret-key}")
    private String accessTokenSecretKey;

    @Value("${jwt.refresh-token-secret-key}")
    private String refreshTokenSecretKey;

    @Value("${jwt.access-token-expiration-msec}")
    private long accessTokenExpirationMsec;

    @Value("${jwt.refresh-token-expiration-msec}")
    private long refreshTokenExpirationMsec;

    private final UserDetailsServiceImpl userDetailsService;
    private final TokenServiceImpl tokenService;

    /**
     * 액세스 토큰 & 리프레시 토큰 생성
     */
    public JwtResponse generateToken(Authentication authentication) {
        Date expiryDate = this.createExpiryDate(accessTokenExpirationMsec);
        String accessToken = this.createAccessToken(authentication, accessTokenExpirationMsec);
        String refreshToken = this.createRefreshToken(authentication, refreshTokenExpirationMsec);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .accessTokenExp(expiryDate.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(Authentication authentication, long tokenExpiryDate) {
        return this.createToken(authentication, tokenExpiryDate, accessTokenSecretKey);
    }

    public String createRefreshToken(Authentication authentication, long tokenExpiryDate) {
        String token = this.createToken(authentication, tokenExpiryDate, refreshTokenSecretKey);
        Date expiryDate = this.createExpiryDate(refreshTokenExpirationMsec);
        this.tokenService.createRefreshToken(token, expiryDate, authentication);

        return token;
    }

    public String createToken(Authentication authentication, long tokenExpiryDate, String secretKey) {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .claim("auth", role)
                .subject(authentication.getName())
                .issuedAt(new Date())
                .expiration(createExpiryDate(tokenExpiryDate))
                .signWith(createKey(secretKey))
                .compact();
    }

    public Date createExpiryDate(long tokenExpiryDate) {
        return new Date(new Date().getTime() + tokenExpiryDate);
    }

    public SecretKey createKey(String tokenSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
    }

    // 토큰에서 사용자 이름(username) 추출
    public String getUsernameFromToken(String token, String type) {
        String secretKey = type.equals(TokenType.ACCESS_TOKEN.getValue()) ? accessTokenSecretKey : refreshTokenSecretKey;

        return Jwts.parser()
                .verifyWith(createKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰에서 사용자 이름을 추출한 후 해당 사용자 조회
    public Authentication getAuthentication(String token, String type) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(getUsernameFromToken(token, type));
        return new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities());
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(createKey(accessTokenSecretKey)).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith(createKey(refreshTokenSecretKey)).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }
}

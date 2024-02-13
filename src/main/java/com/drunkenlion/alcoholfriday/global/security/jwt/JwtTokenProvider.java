package com.drunkenlion.alcoholfriday.global.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.drunkenlion.alcoholfriday.global.security.auth.UserDetailsServiceImpl;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;
import com.drunkenlion.alcoholfriday.global.security.jwt.entity.RefreshToken;

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
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-msec}")
    private long accessTokenExpirationMsec;

    @Value("${jwt.refresh-token-expiration-msec}")
    private long refreshTokenExpirationMsec;

    private final UserDetailsServiceImpl userDetailsService;

    public SecretKey createKey(String tokenSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
    }

    public JwtResponse createAccessToken(Authentication authentication) {
        String token = createToken(authentication, accessTokenExpirationMsec);
        Date expiryDate = createExpiryDate(accessTokenExpirationMsec);

        return JwtResponse.builder()
                .accessToken(token)
                .accessTokenExp(expiryDate.getTime())
                .build();
    }

    public RefreshToken createRefreshToken(Authentication authentication) {
        String token = createToken(authentication, refreshTokenExpirationMsec);

        return RefreshToken.builder()
                .token(token)
                .expiryDate(createExpiryDate(refreshTokenExpirationMsec).toInstant())
                .build();
    }

    public String createToken(Authentication authentication, long tokenExpiryDate) {
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

    // 토큰에서 사용자 이름(username) 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(createKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰에서 사용자 이름을 추출한 후 해당 사용자 조회
    public Authentication getAuthentication(String token) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(createKey(secretKey)).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }
}

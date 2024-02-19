package com.drunkenlion.alcoholfriday.global.security.jwt.dto;

import com.drunkenlion.alcoholfriday.domain.auth.dto.LoginResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "토큰 정보")
public class JwtResponse {
    @Schema(description = "액세스 토큰")
    private final String accessToken;

    @Schema(description = "액세스 토큰 만료 일자")
    private final Long accessTokenExp;

    @Schema(description = "리프레시 토큰")
    private final String refreshToken;
}

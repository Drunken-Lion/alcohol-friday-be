package com.drunkenlion.alcoholfriday.domain.auth.dto;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.global.security.jwt.dto.JwtResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "로그인 정보")
public class LoginResponse {
    @Schema(description = "회원 정보")
    private MemberResponse memberResponse;

    @Schema(description = "토큰 정보")
    private JwtResponse jwtResponse;
}

package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import java.time.LocalDateTime;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 정보")
public class MemberResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Email
    @Schema(description = "이메일", example = "example@example.com")
    private String email;

    @Schema(description = "이름", example = "김철수")
    private String name;

    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "연락처", example = "1012345678")
    private Long phone;

    @Schema(description = "소셜 로그인 제공처", example = "kakao")
    private String provider;

    @Schema(description = "회원 권한", example = "MEMBER")
    private MemberRole role;

    @Schema(description = "회원 가입 일시")
    private LocalDateTime createdAt;

    @Schema(description = "회원 정보 수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "회원 탈퇴 일시")
    private LocalDateTime deletedAt;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .provider(member.getProvider().getProviderName())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .deletedAt(member.getDeletedAt())
                .build();
    }
}

package com.drunkenlion.alcoholfriday.domain.admin.member.dto;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 회원 조회 항목")
public class MemberListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "별명")
    private String nickname;

    @Email
    @Schema(description = "이메일", example = "example@example.com")
    private String email;

    @Schema(description = "권한")
    private MemberRole role;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static MemberListResponse of(Member member) {
        return MemberListResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .deleted(member.getDeletedAt() != null)
                .build();
    }
}

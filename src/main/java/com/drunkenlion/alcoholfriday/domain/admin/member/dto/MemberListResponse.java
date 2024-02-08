package com.drunkenlion.alcoholfriday.domain.admin.member.dto;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberListResponse {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String role;
    private LocalDateTime createdAt;
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

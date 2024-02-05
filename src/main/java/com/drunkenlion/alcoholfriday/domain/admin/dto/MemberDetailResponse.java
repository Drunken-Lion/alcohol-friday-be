package com.drunkenlion.alcoholfriday.domain.admin.dto;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberDetailResponse {
    private Long id;
    private String email;
    private String provider;
    private String name;
    private String nickname;
    private String role;
    private Long phone;
    private LocalDate certifyAt;
    private Boolean agreedToServiceUse;
    private Boolean agreedToServicePolicy;
    private Boolean agreedToServicePolicyUse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static MemberDetailResponse of(Member member) {
        return MemberDetailResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .provider(member.getProvider())
                .name(member.getName())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phone(member.getPhone())
                .certifyAt(member.getCertifyAt())
                .agreedToServiceUse(member.getAgreedToServiceUse())
                .agreedToServicePolicy(member.getAgreedToServicePolicy())
                .agreedToServicePolicyUse(member.getAgreedToServicePolicyUse())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .deletedAt(member.getDeletedAt())
                .build();
    }
}

package com.drunkenlion.alcoholfriday.domain.admin.member.dto;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 상세 조회 항목")
public class MemberDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Email
    @Schema(description = "이메일", example = "example@example.com")
    private String email;

    @Schema(description = "가입 소셜 정보")
    private String provider;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "별명")
    private String nickname;

    @Schema(description = "권한")
    private MemberRole role;

    @Schema(description = "연락처")
    private Long phone;

    @Schema(description = "성인인증 날짜")
    private LocalDate certifyAt;

    @Schema(description = "이용 약관 동의")
    private Boolean agreedToServiceUse;

    @Schema(description = "개인정보 수집 이용 안내 동의")
    private Boolean agreedToServicePolicy;

    @Schema(description = "개인정보 활용 동의")
    private Boolean agreedToServicePolicyUse;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static MemberDetailResponse of(Member member) {
        return MemberDetailResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .provider(member.getProvider().getProviderName())
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

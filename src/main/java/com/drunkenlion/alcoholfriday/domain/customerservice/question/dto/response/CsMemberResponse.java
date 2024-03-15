package com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response;

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
@Schema(description = "고객센터의 사용자 정보")
public class CsMemberResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "이름", example = "김철수")
    private String name;

    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;

    @Email
    @Schema(description = "이메일", example = "example@example.com")
    private String email;

    public static CsMemberResponse of(Member member) {
        return CsMemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .build();
    }
}

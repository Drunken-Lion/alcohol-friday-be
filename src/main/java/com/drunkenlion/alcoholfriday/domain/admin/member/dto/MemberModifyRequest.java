package com.drunkenlion.alcoholfriday.domain.admin.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 수정 항목")
public class MemberModifyRequest {
    @Schema(description = "별명")
    private String nickname;

    @Schema(description = "권한")
    private String role;

    @Schema(description = "연락처")
    private Long phone;
}

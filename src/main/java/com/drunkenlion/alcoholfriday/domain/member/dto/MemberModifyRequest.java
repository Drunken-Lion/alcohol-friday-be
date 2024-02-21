package com.drunkenlion.alcoholfriday.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "마이페이지 회원 수정 요청 항목")
public class MemberModifyRequest {
    @Schema(description = "별명")
    private String nickname;

    @Schema(description = "연락처")
    private Long phone;
}

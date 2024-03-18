package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "공지사항 등록 요청")
public class NoticeSaveRequest {
    @NotBlank(message = "공지사항의 제목이 존재하지 않습니다.")
    @Schema(description = "공지사항 제목")
    private String title;

    @NotBlank(message = "공지사항의 내용이 존재하지 않습니다.")
    @Schema(description = "공지사항 내용")
    private String content;

    public static Notice toEntity(NoticeSaveRequest request, Member member) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();
    }
}

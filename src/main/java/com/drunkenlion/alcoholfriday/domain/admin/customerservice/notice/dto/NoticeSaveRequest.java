package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto;

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
}

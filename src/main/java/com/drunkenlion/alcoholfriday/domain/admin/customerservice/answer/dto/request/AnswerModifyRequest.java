package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "문의사항 댓글 수정 요청")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerModifyRequest {
    @Schema(description = "댓글 수정 내용")
    private String updateContent;
}

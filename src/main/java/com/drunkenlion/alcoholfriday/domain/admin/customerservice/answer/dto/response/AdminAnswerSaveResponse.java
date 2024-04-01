package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.CsMemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "문의사항 답변 응답")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAnswerSaveResponse {
    @Schema(description = "문의사항 답변 고유 식별 번호")
    private Long id;

    @Schema(description = "문의사항 답변 내용")
    private String content;

    @Schema(description = "문의사항 답변 작성 일자")
    private LocalDateTime createdAt;

    @Schema(description = "문의사항 답변 작성자")
    private CsMemberResponse member;

    public static AdminAnswerSaveResponse of(Answer answer) {
        return AdminAnswerSaveResponse.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .createdAt(answer.getCreatedAt())
                .member(CsMemberResponse.of(answer.getMember()))
                .build();
    }
}

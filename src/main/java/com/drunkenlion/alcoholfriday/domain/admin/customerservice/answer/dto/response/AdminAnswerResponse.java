package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.CsMemberResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "관리자 문의사항 답변 응답")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAnswerResponse {
    @Schema(description = "문의사항 답변 고유 식별 번호")
    private Long id;

    @Schema(description = "문의사항 답변 내용")
    private String content;

    @Schema(description = "문의사항 답변 작성 일자")
    private LocalDateTime createdAt;

    @Schema(description = "문의사항 답변 작성자")
    private CsMemberResponse member;

    public static AdminAnswerResponse of(Answer answer) {
        return AdminAnswerResponse.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .createdAt(answer.getCreatedAt())
                .member(CsMemberResponse.of(answer.getMember()))
                .build();
    }
}

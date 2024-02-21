package com.drunkenlion.alcoholfriday.domain.customerservice.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "문의사항 답변 응답")
public class AnswerResponse {
    @Schema(description = "문의사항 답변 고유 식별 번호")
    private Long id;

    @Schema(description = "문의사항 답변 작성자")
    private Member member;

    @Schema(description = "문의사항 답변 내용")
    private String content;

    public static AnswerResponse of(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .member(answer.getMember())
                .content(answer.getContent())
                .build();
    }
}
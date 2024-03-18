package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "문의사항 댓글 작성 등록 요청")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerSaveRequest {
    @Schema(description = "문의사항 고유 식별 번호")
    private Long questionId;

    @Schema(description = "댓글 내용")
    private String content;

    public static Answer toEntity(AnswerSaveRequest request, Question question, Member member) {
        return Answer.builder()
                .question(question)
                .content(request.getContent())
                .member(member)
                .build();
    }
}

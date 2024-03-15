package com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "문의사항 등록 요청 항목")
public class QuestionSaveRequest {
    @NotBlank(message = "문의의 제목이 존재하지 않습니다.")
    @Schema(description = "문의사항 제목")
    private String title;

    @NotBlank(message = "문의의 내용이 존재하지 않습니다.")
    @Schema(description = "문의사항 내용")
    private String content;

    public static Question toEntity(QuestionSaveRequest request, Member member) {
        return Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(QuestionStatus.INCOMPLETE)
                .member(member)
                .build();
    }
}

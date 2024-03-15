package com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.dto.response.AnswerResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "문의사항 응답")
public class QuestionResponse {
    @Schema(description = "문의사항 고유 식별 번호")
    private Long id;

    @Schema(description = "문의사항 제목")
    private String title;

    @Schema(description = "문의사항 내용")
    private String content;

    @Schema(description = "문의사항 답변 상태")
    private QuestionStatus status;

    @Schema(description = "생성 일자")
    private LocalDateTime createdAt;

    @Schema(description = "문의사항 작성자")
    private CsMemberResponse member;

    @Schema(description = "문의사항 등록 이미지")
    private NcpFileResponse file;

    @Schema(description = "문의사항에 등록된 답변")
    private List<AnswerResponse> answers;

    public static QuestionResponse of(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .member(CsMemberResponse.of(question.getMember()))
                .title(question.getTitle())
                .content(question.getContent())
                .status(question.getStatus())
                .answers(question.getAnswers().stream().map(AnswerResponse::of).collect(Collectors.toList()))
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static QuestionResponse of(Question question, NcpFileResponse file) {
        return QuestionResponse.builder()
                .id(question.getId())
                .member(CsMemberResponse.of(question.getMember()))
                .title(question.getTitle())
                .content(question.getContent())
                .status(question.getStatus())
                .answers(question.getAnswers().stream().map(AnswerResponse::of).collect(Collectors.toList()))
                .createdAt(question.getCreatedAt())
                .file(file)
                .build();
    }
}

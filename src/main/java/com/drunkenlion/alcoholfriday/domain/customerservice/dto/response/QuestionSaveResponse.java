package com.drunkenlion.alcoholfriday.domain.customerservice.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
public class QuestionSaveResponse {
    @Schema(description = "문의사항 고유 식별 번호")
    private Long id;

    @Schema(description = "문의사항 작성자")
    private Member member;

    @Schema(description = "문의사항 제목")
    private String title;

    @Schema(description = "문의사항 내용")
    private String content;

    public static QuestionSaveResponse of(Question question) {
        return QuestionSaveResponse.builder()
                .id(question.getId())
                .member(question.getMember())
                .title(question.getTitle())
                .content(question.getContent())
                .build();
    }
}
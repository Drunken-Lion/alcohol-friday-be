package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "나의 문의 내역 리스트 항목")
public class MemberQuestionListResponse {
    @Schema(description = "문의 내역 고유 아이디")
    private Long id;

    @Schema(description = "문의 제목")
    private String title;

    @Schema(description = "답변 여부")
    private String questionStatus;

    @Schema(description = "문의 작성일")
    private LocalDateTime createdAt;

    public static MemberQuestionListResponse of(Question question) {
        return MemberQuestionListResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .questionStatus(question.getStatus().getLabel())
                .createdAt(question.getCreatedAt())
                .build();
    }
}

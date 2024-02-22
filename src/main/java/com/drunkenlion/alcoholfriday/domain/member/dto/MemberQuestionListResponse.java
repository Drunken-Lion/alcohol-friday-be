package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberQuestionListResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;

    public static MemberQuestionListResponse of(Question question) {
        return MemberQuestionListResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .createdAt(question.getCreatedAt())
                .build();
    }
}

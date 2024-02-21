package com.drunkenlion.alcoholfriday.domain.customerservice.dto.request;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "문의사항 등록 요청 항목")
public class QuestionRequest {
    @Schema(description = "문의사항 제목")
    private String title;

    @Schema(description = "문의사항 내용")
    private String content;

    @Schema(description = "문의사항 등록 이미지")
    private List<MultipartFile> files;

    public static Question toEntity(QuestionRequest request, Member member) {
        return Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();
    }
}

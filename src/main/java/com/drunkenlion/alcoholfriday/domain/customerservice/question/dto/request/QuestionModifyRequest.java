package com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request;

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
@Schema(description = "문의사항 수정 요청 항목")
public class QuestionModifyRequest {
    @Schema(description = "문의사항 제목")
    private String updateTitle;

    @Schema(description = "문의사항 내용")
    private String updateContent;

    @Schema(description = "삭제할 이미지 seq list")
    private List<Integer> removeImageSeqList;
}

package com.drunkenlion.alcoholfriday.domain.customerservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
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
@Schema(description = "문의사항 등록 요청 항목")
public class QuestionModifyRequest {
    @NotBlank(message = "문의의 제목이 존재하지 않습니다.")
    @Schema(description = "문의사항 제목")
    private String updateTitle;

    @NotBlank(message = "문의의 내용이 존재하지 않습니다.")
    @Schema(description = "문의사항 내용")
    private String updateContent;

    @Builder.Default
    @Schema(description = "삭제할 이미지 seq list")
    private List<Integer> removeImageSeqList = new ArrayList<>();
}

package com.drunkenlion.alcoholfriday.domain.review.dto.response;

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
@Schema(description = "리뷰 수정 요청 항목")
public class ReviewModifyRequest {
    @Schema(description = "리뷰 수정 점수")
    private Double updateScore;

    @Schema(description = "리뷰 수정 내용")
    private String updateContent;

    @Schema(description = "삭제할 이미지 seq list")
    private List<Integer> removeImageSeqList;
}

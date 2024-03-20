package com.drunkenlion.alcoholfriday.domain.review.dto.response;

import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewModifyResponse {
    @Schema(description = "리뷰 상품 고유 식별 ID")
    private Long id;

    @Schema(description = "리뷰 점수")
    private Double score;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 작성자")
    private ReviewMemberResponse member;

    @Schema(description = "리뷰 첨부 이미지")
    private NcpFileResponse files;

    public static ReviewModifyResponse of(Review review, NcpFileResponse files) {
        return ReviewModifyResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .member(ReviewMemberResponse.of(review.getMember()))
                .files(files)
                .build();
    }
}

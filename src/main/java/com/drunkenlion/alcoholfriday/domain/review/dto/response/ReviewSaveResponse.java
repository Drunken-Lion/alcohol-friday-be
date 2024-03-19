package com.drunkenlion.alcoholfriday.domain.review.dto.response;

import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "리뷰 등록 응답")
public class ReviewSaveResponse {
    @Schema(description = "리뷰 고유 식별 ID")
    private Long id;

    @Schema(description = "리뷰 별점")
    private Double score;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 작성 일자")
    private LocalDateTime createdAt;

    @Schema(description = "리뷰 등록 회원 정보")
    private ReviewMemberResponse member;

    @Schema(description = "리뷰 등록 사진 정보")
    private NcpFileResponse files;

    public static ReviewSaveResponse of(Review review, NcpFileResponse files) {
        return ReviewSaveResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .member(ReviewMemberResponse.of(review.getMember()))
                .files(files)
                .build();
    }
}

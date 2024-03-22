package com.drunkenlion.alcoholfriday.domain.item.dto;

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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "상품 상세페이지 리뷰 응답")
public class ItemReviewResponse {
    @Schema(description = "리뷰 고유 식별 ID")
    private Long id;

    @Schema(description = "리뷰 작성자 별명")
    private String nickname;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 점수")
    private Double score;

    @Schema(description = "작성 일자")
    private LocalDateTime createdAt;

    @Schema(description = "리뷰 사진 정보")
    private NcpFileResponse files;

    public static ItemReviewResponse of(Review review, NcpFileResponse files) {
        return ItemReviewResponse.builder()
                .id(review.getId())
                .nickname(review.getMember().getNickname())
                .content(review.getContent())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .files(files)
                .build();
    }
}

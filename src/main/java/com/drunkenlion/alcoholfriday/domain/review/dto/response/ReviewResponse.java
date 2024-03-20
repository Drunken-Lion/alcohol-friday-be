package com.drunkenlion.alcoholfriday.domain.review.dto.response;

import com.drunkenlion.alcoholfriday.domain.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "리뷰 정보")
public class ReviewResponse {
    @Schema(description = "리뷰 고유 아이디")
    private Long id;

    @Schema(description = "별점")
    private Double score;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "해당 리뷰가 작성된 주문 정보")
    private ReviewOrderDetailResponse orderDetail;

    @Schema(description = "해당 리뷰의 첨부된 이미지")
    private NcpFileResponse files;
    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .orderDetail(ReviewOrderDetailResponse.of(review.getOrderDetail()))
                .build();
    }
    public static ReviewResponse of(Review review, NcpFileResponse files) {
        return ReviewResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .orderDetail(ReviewOrderDetailResponse.of(review.getOrderDetail()))
                .files(files)
                .build();
    }
}

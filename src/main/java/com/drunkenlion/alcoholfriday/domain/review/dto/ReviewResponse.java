package com.drunkenlion.alcoholfriday.domain.review.dto;

import com.drunkenlion.alcoholfriday.domain.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "")
public class ReviewResponse {
    private Long id;
    private Long score;
    private String content;
    private OrderDetailResponse productInfo;

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .productInfo(OrderDetailResponse.of(review.getOrderDetail()))
                .build();
    }
}

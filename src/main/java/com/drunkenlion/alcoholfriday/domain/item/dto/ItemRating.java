package com.drunkenlion.alcoholfriday.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "상품 총 평점 / 상품 리뷰 총 개수")
public class ItemRating {
    @Schema(description = "리뷰 아이템 고유 번호")
    private Long itemId;

    @Schema(description = "한 개 상품 총 평점")
    private Double avgItemScore;

    @Schema(description = "한 개 상품 리뷰 총 개수")
    private int totalReviewCount;
}

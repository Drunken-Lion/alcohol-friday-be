package com.drunkenlion.alcoholfriday.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "나의 리뷰 정보")
public class MemberReviewResponse<T> {
    @Schema(description = "주문 상품에 대한 리뷰 상태 표시 (미작성 or 작성)")
    private String status;

    @Schema(description = "리뷰 미작성인 주문 상품 정보 or 작성한 리뷰 정보")
    private T response;

    public static <T> MemberReviewResponse<T> of(String status, T response) {
        return MemberReviewResponse.<T>builder()
                .status(status)
                .response(response)
                .build();
    }

}

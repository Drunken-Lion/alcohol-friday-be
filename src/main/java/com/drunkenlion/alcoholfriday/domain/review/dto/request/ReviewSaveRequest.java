package com.drunkenlion.alcoholfriday.domain.review.dto.request;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "리뷰 등록 요청 항목")
public class ReviewSaveRequest {
    @NotNull
    @Schema(description = "리뷰 상품 고유 식별 ID")
    private Long orderDetailId;

    @NotNull
    @Schema(description = "리뷰 점수")
    private Double score;

    @NotBlank
    @Schema(description = "리뷰 내용")
    private String content;

    public static Review toEntity(ReviewSaveRequest request, Member member) {
        return Review.builder()
                .member(member)
                .content(request.getContent())
                .score(request.score)
                .build();
    }
}

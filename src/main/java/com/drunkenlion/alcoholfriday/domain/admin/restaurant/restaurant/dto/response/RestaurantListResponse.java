package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 매장 조회 항목")
public class RestaurantListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "회원 별명")
    private String memberNickname;

    @Schema(description = "매장 이름")
    private String name;

    @Schema(description = "매장 카테고리")
    private String category;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static RestaurantListResponse of(Restaurant restaurant) {
        return RestaurantListResponse.builder()
                .id(restaurant.getId())
                .memberNickname(restaurant.getMember().getNickname())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .createdAt(restaurant.getCreatedAt())
                .deleted(restaurant.getDeletedAt() != null)
                .build();
    }
}

package com.drunkenlion.alcoholfriday.domain.admin.dto;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RestaurantListResponse {
    private Long id;
    private String memberNickname;
    private String name;
    private String category;
    private LocalDateTime createdAt;
    private boolean deleted;

    public static RestaurantListResponse of(Restaurant restaurant) {
        return RestaurantListResponse.builder()
                .id(restaurant.getId())
                .memberNickname(restaurant.getMembers().getNickname())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .createdAt(restaurant.getCreatedAt())
                .deleted(restaurant.getDeletedAt() != null)
                .build();
    }
}

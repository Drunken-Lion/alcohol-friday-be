package com.drunkenlion.alcoholfriday.domain.admin.dto;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import lombok.*;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RestaurantDetailResponse {
    private Long id;
    private Long memberId;
    private String memberNickname;
    private String name;
    private String category;
    private String address;
    private Point location;
    private Long contact;
    private Map<String, Object> menu;
    private Map<String, Object> time;
    private LocalDateTime createdAt;
    private boolean deleted;

    public static RestaurantDetailResponse of(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .id(restaurant.getId())
                .memberNickname(restaurant.getMembers().getNickname())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .createdAt(restaurant.getCreatedAt())
                .deleted(restaurant.getDeletedAt() != null)
                .build();
    }
}

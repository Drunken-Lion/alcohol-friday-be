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
    private Map<String, Object> provision;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

// TODO: 판매하는 술정보 추가 필요
//    private List<Item> item;

    public static RestaurantDetailResponse of(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .id(restaurant.getId())
                .memberId(restaurant.getMembers().getId())
                .memberNickname(restaurant.getMembers().getNickname())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .location(restaurant.getLocation())
                .contact(restaurant.getContact())
                .menu(restaurant.getMenu())
                .time(restaurant.getTime())
                .provision(restaurant.getProvision())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .deletedAt(restaurant.getDeletedAt())
                .build();
    }
}

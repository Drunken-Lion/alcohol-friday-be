package com.drunkenlion.alcoholfriday.domain.restaurant.dto.response;


import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.locationtech.jts.geom.Point;


import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 레스토랑 조회 항목")
public class RestaurantLocationResponse {

    @Schema(description = "매장 이름")
    private Long id;

    @Schema(description = "회원 고유아이디")
    private Long memberId;

    @Schema(description = "매장 카테고리")
    private String category;

    @Schema(description = "매장 이름")
    private String name;

    @Schema(description = "매장 주소")
    private String address;

    @Schema(description = "매장 위치(위도 , 경도")
    private Point location;

    @Schema(description = "매장 연락처")
    private Long contact;

    @Schema(description = "매장 목록")
    private Map<String, Object> menu;

    @Schema(description = "영업 시간")
    private Map<String, Object> time;

    @Schema(description = "편의시설 목록")
    private Map<String, Object> provision;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;


    public static RestaurantLocationResponse of(Restaurant restaurant) {
        return RestaurantLocationResponse.builder()
                .id(restaurant.getId())
                .memberId(restaurant.getMembers().getId())
                .category(restaurant.getCategory())
                .name(restaurant.getName())
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

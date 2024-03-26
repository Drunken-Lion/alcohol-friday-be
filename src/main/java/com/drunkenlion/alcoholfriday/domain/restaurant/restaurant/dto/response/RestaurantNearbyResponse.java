package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.text.DecimalFormat;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑의 주변에 제품을 취급에 대한 응답")
public class RestaurantNearbyResponse {

    @Schema(description = "레스토랑 고유아이디")
    private Long restaurantId;

    @Schema(description = "사용자 위치로부터의 거리 (Km 단위)")
    private Double distanceKm;

    @Schema(description = "제품의 이름")
    private String productName;

    @Schema(description = "매장 이름")
    private String restaurantName;

    @Schema(description = "매장 주소")
    private String address;

    public RestaurantNearbyResponse(Long restaurantId, String restaurantName, String address, String productName, Double distance) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.productName = productName;
        this.distanceKm = Double.parseDouble(new DecimalFormat("0.0").format(distance / 1_000));
    }
}

package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.util.RestaurantConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 지도 반환 객체")
public class RestaurantMapResponse {
    @Schema(description = "매장 고유 식별 ID")
    private Long restaurantId;

    @Schema(description = "매장 위치(위도)")
    private Double latitude;

    @Schema(description = "매장 위치(경도)")
    private Double longitude;

    @Schema(description = "매장 이름")
    private String restaurantName;

    @Schema(description = "매장 카테고리")
    private String restaurantCategory;

    @Schema(description = "매장 주소")
    private String restaurantAddress;

    @Schema(description = "매장 영업 여부")
    private String businessStatus;

    @Schema(description = "매장 편의 시설")
    private List<Provision> provision;

    @Schema(description = "매장 취급 상품 정보")
    private List<RestaurantSimpleProductResponse> restaurantProducts;

    public static RestaurantMapResponse of(Restaurant restaurant, List<RestaurantSimpleProductResponse> restaurantProducts) {
        return RestaurantMapResponse.builder()
                .restaurantId(restaurant.getId())
                .latitude(restaurant.getLocation().getY())
                .longitude(restaurant.getLocation().getX())
                .restaurantName(restaurant.getName())
                .restaurantCategory(restaurant.getCategory())
                .restaurantAddress(restaurant.getAddress())
                .businessStatus(RestaurantConvertor.getBusinessStatus(restaurant))
                .provision(Provision.getProvisions(restaurant))
                .restaurantProducts(restaurantProducts)
                .build();
    }
}

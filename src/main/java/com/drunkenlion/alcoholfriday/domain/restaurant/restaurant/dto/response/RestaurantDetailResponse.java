package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.util.RestaurantConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 상세 정보 반환 객체")
public class RestaurantDetailResponse {
    @Schema(description = "매장 고유 식별 ID")
    private Long restaurantId;

    @Schema(description = "매장 이름")
    private String restaurantName;

    @Schema(description = "매장 메뉴 목록")
    private List<String> restaurantMenu;

    @Schema(description = "매장 주소")
    private String restaurantAddress;

    @Schema(description = "매장 영업 여부")
    private String businessStatus;

    @Schema(description = "매장 연락처")
    private Long restaurantContactNumber;

    @Schema(description = "매장 편의 시설")
    private List<Provision> provision;

    @Schema(description = "매장 요일 별 영업 시간")
    private Map<String, Object> businessTime;
    public static RestaurantDetailResponse of(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .restaurantAddress(restaurant.getAddress())
                .restaurantContactNumber(restaurant.getContact())
                .businessStatus(RestaurantConvertor.getBusinessStatus(restaurant))
                .businessTime(RestaurantConvertor.getBusinessTime(restaurant))
                .provision(Provision.getProvisions(restaurant))
                .restaurantMenu(restaurant.getMenu().keySet().stream().toList())
                .build();
    }
}

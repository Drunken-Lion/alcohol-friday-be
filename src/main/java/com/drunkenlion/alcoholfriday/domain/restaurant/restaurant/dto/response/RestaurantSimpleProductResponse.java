package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 취급 주류 간략 정보 반환 객체")
public class RestaurantSimpleProductResponse {
    @Schema(description = "제품 고유 식별 ID")
    private Long id;

    @Schema(description = "제품 이름")
    private String name;

    @Schema(description = "제품 이미지")
    private NcpFileResponse files;

    public static RestaurantSimpleProductResponse of(RestaurantStock restaurantStock, NcpFileResponse files) {
        return RestaurantSimpleProductResponse.builder()
                .id(restaurantStock.getProduct().getId())
                .name(restaurantStock.getProduct().getName())
                .files(files)
                .build();
    }
}

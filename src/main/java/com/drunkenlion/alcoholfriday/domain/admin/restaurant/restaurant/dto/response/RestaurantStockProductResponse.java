package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 상품 간략 정보")
public class RestaurantStockProductResponse {
    @Schema(description = "매장 재고 상품 고유 아이디")
    private Long stockProductId;

    @Schema(description = "매장 재고 상품 이름")
    private String stockProductName;

    @Schema(description = "매장 재고량")
    private Long stockQuantity;

    @Schema(description = "매장 재고 상품의 첫번째 이미지")
    private NcpFileResponse stockProductFile;

    public static RestaurantStockProductResponse of(RestaurantStock restaurantStock, NcpFileResponse file) {
        return RestaurantStockProductResponse.builder()
                .stockProductId(restaurantStock.getProduct().getId())
                .stockProductName(restaurantStock.getProduct().getName())
                .stockQuantity(restaurantStock.getQuantity())
                .stockProductFile(file)
                .build();
    }
}

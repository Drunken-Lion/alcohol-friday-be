package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "수정한 재고 정보 응답 항목")
public class RestaurantStockModifyResponse {
    @Schema(description = "매장 재고 고유 아이디")
    private Long id;

    @Schema(description = "제품명")
    private String name;

    @Schema(description = "제품 판매 단가")
    private BigDecimal price;

    @Schema(description = "재고 수량")
    private Long quantity;

    public static RestaurantStockModifyResponse of(RestaurantStock stock) {
        return RestaurantStockModifyResponse.builder()
                .id(stock.getId())
                .name(stock.getProduct().getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .build();
    }
}

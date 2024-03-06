package com.drunkenlion.alcoholfriday.domain.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import lombok.*;


@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantStockResponse {

    private ItemResponse item;

    private Long quantity;

    public static RestaurantStockResponse of(RestaurantStock stock){
        return RestaurantStockResponse.builder()
                .item(ItemResponse.of(stock.getItem()))
                .quantity(stock.getQuantity())
                .build();
    }
}

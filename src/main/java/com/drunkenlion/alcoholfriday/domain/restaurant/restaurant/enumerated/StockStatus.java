package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockStatus {
    IN_STOCK("재고있음"),
    NO_STOCK("재고없음"),
    ;

    private final String status;

    public static String getStockStatus(RestaurantStock restaurantStock) {
        if (restaurantStock.getQuantity() > 0) {
            return StockStatus.IN_STOCK.getStatus();
        }
        return StockStatus.NO_STOCK.getStatus();
    }
}

package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import java.util.Optional;

public interface RestaurantStockCustomRepository {
    Optional<RestaurantStock> findRestaurantAndProduct(Restaurant restaurant, Product product);
}

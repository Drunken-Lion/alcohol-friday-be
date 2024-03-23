package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantStockCustomRepository {
    Optional<RestaurantStock> findRestaurantAndProduct(Restaurant restaurant, Product product);

    Page<RestaurantStock> findRestaurantStock(Long id, Pageable pageable);
}

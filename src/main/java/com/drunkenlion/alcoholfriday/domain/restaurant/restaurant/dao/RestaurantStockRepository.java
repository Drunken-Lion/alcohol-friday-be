package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantStockRepository extends JpaRepository<RestaurantStock, Long>, RestaurantStockCustomRepository {
    List<RestaurantStock> findByRestaurantAndDeletedAtIsNull(Restaurant restaurant);

    Optional<RestaurantStock> findByRestaurantIdAndProductIdAndDeletedAtIsNull(Long restaurantId, Long productId);

    Optional<RestaurantStock> findByIdAndDeletedAtIsNull(Long stockId);
}

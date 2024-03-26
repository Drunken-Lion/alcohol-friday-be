package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantStockRepository extends JpaRepository<RestaurantStock, Long>, RestaurantStockCustomRepository {
    List<RestaurantStock> findByRestaurantAndDeletedAtIsNull(Restaurant restaurant);
    Optional<RestaurantStock> findByRestaurantIdAndProductIdAndDeletedAtIsNull(Long restaurantId, Long productId);
}

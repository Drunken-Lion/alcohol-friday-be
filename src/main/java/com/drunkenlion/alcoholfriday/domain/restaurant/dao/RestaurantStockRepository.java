package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantStockRepository extends JpaRepository<RestaurantStock, Long> {
}

package com.drunkenlion.alcoholfriday.domain.restaurant.cart.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderCartRepository extends JpaRepository<RestaurantOrderCart, Long>,RestaurantOrderCartCustomRepository {

}

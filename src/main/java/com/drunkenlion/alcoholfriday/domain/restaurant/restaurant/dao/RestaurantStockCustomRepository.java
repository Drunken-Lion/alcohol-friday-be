package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantStockCustomRepository {
    Optional<RestaurantStock> findRestaurantAndProduct(Restaurant restaurant, Product product);

    Page<RestaurantStock> findRestaurantStock(Long id, Pageable pageable);
    
    Page<RestaurantStock> findRestaurantStocks(Member member, Restaurant restaurant, Pageable pageable);
}

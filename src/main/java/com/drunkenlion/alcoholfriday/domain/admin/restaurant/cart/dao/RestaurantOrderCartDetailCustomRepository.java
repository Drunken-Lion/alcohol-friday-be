package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantOrderCartDetailCustomRepository {
    List<RestaurantOrderCartDetail> findRestaurantAndMember(Restaurant restaurant, Member member);
    Page<RestaurantOrderCartDetail> findRestaurantAndMember(Restaurant restaurant, Member member, Pageable pageable);
    Optional<RestaurantOrderCartDetail> findCartAndProduct(RestaurantOrderCart cart, Product product);
}

package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import java.util.Optional;

public interface RestaurantOrderCartCustomRepository {

    Optional<RestaurantOrderCart> findRestaurantAndMember(Restaurant restaurant, Member member);
}

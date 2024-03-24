package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;


import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.QRestaurantOrderCart.restaurantOrderCart;

@RequiredArgsConstructor
public class RestaurantOrderCartCustomRepositoryImpl implements RestaurantOrderCartCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RestaurantOrderCart> findRestaurantAndMember(Restaurant restaurant, Member member) {
        BooleanExpression conditions =
                restaurantOrderCart.restaurant.eq(restaurant)
                        .and(restaurantOrderCart.member.eq(member))
                        .and(restaurantOrderCart.deletedAt.isNull());

        return Optional.ofNullable(queryFactory.
                selectFrom(restaurantOrderCart)
                .where(conditions)
                .fetchFirst());
    }
}

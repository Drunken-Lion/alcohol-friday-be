package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.QRestaurantOrderCart.restaurantOrderCart;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantOrderCartCustomRepositoryImpl implements RestaurantOrderCartCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Optional<RestaurantOrderCart> findRestaurantAndMember(Restaurant restaurant, Member member) {
        BooleanExpression conditions =
                restaurantOrderCart.restaurant.eq(restaurant)
                        .and(restaurantOrderCart.member.eq(member))
                        .and(restaurantOrderCart.deletedAt.isNull());

        return Optional.ofNullable(query.
                selectFrom(restaurantOrderCart)
                .where(conditions)
                .fetchFirst());
    }
}

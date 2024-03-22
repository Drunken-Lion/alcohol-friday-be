package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurantStock.restaurantStock;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantStockCustomRepositoryImpl implements RestaurantStockCustomRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RestaurantStock> findRestaurantAndProduct(Restaurant restaurant, Product product) {
        BooleanExpression conditions =
                restaurantStock.restaurant.eq(restaurant)
                        .and(restaurantStock.product.eq(product))
                        .and(restaurantStock.deletedAt.isNull());

        return Optional.ofNullable(queryFactory
                .selectFrom(restaurantStock)
                .where(conditions)
                .fetchFirst()
        );
    }
}

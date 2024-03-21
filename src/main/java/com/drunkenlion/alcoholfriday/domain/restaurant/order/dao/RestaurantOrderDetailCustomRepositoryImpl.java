package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;

import static com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.QRestaurantOrder.restaurantOrder;

import static com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.QRestaurantOrderDetail.restaurantOrderDetail;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrderDetail;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantOrderDetailCustomRepositoryImpl implements RestaurantOrderDetailCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RestaurantOrderDetail> findRestaurantOrderAndProduct(RestaurantOrder order, Product product) {
        BooleanExpression conditions =
                restaurantOrderDetail.restaurantOrder.eq(order)
                        .and(restaurantOrderDetail.product.eq(product))
                        .and(restaurantOrderDetail.deletedAt.isNull())
                        .and(restaurantOrderDetail.restaurantOrder.deletedAt.isNull());

        return Optional.ofNullable(queryFactory
                .selectFrom(restaurantOrderDetail)
                .where(conditions)
                .fetchOne());
    }
}

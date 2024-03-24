package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;


import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrderDetail.restaurantOrderDetail;

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

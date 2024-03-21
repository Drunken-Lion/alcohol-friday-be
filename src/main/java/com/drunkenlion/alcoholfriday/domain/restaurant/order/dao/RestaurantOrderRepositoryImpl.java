package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import static com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.QRestaurantOrder.restaurantOrder;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.enumerated.RestaurantOrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantOrderRepositoryImpl implements RestaurantOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<RestaurantOrder> findRestaurantOrderOwner(Long id) {
        BooleanExpression conditions =
                restaurantOrder.id.eq(id)
                        .and(restaurantOrder.orderStatus.eq(RestaurantOrderStatus.ADD_INFO))
                        .and(restaurantOrder.deletedAt.isNull());

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(restaurantOrder)
                .where(conditions)
                .fetchFirst());
    }

    @Override
    public Optional<RestaurantOrder> findRestaurantOrderAdmin(Long id) {
        BooleanExpression conditions =
                restaurantOrder.id.eq(id)
                        .and(restaurantOrder.orderStatus.eq(RestaurantOrderStatus.WAITING_APPROVAL))
                        .and(restaurantOrder.deletedAt.isNull());

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(restaurantOrder)
                .where(conditions)
                .fetchFirst());
    }

    @Override
    public List<RestaurantOrder> findOrderToDelete() {
        BooleanExpression conditions =
                restaurantOrder.createdAt.before(LocalDateTime.now().minusMinutes(30))
                        .and(restaurantOrder.orderStatus.eq(RestaurantOrderStatus.ADD_INFO))
                        .and(restaurantOrder.deletedAt.isNull());

        return jpaQueryFactory
                .selectFrom(restaurantOrder)
                .where(conditions)
                .fetch();
    }
}

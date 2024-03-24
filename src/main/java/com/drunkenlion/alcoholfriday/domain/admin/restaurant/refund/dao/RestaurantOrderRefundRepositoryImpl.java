package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrder.restaurantOrder;
import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.QRestaurantOrderRefund.restaurantOrderRefund;
import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.QRestaurantOrderRefundDetail.restaurantOrderRefundDetail;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class RestaurantOrderRefundRepositoryImpl implements RestaurantOrderRefundRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RestaurantOrderRefund> findRefundByRestaurantOrderId(RestaurantOrder requestRestaurantOrder) {
        BooleanBuilder condition = new BooleanBuilder();

        condition.and(restaurantOrderRefund.restaurantOrder.eq(requestRestaurantOrder))
                .and(restaurantOrderRefund.status.eq(RestaurantOrderRefundStatus.COMPLETED))
                .and(restaurantOrderRefund.deletedAt.isNull());

        return jpaQueryFactory
                .select(restaurantOrderRefund)
                .from(restaurantOrderRefund)
                .leftJoin(restaurantOrderRefund.restaurantOrder, restaurantOrder).fetchJoin()
                .leftJoin(restaurantOrderRefund.restaurantOrderRefundDetails, restaurantOrderRefundDetail).fetchJoin()
                .leftJoin(restaurantOrderRefundDetail.product, product).fetchJoin()
                .where(condition)
                .fetch();
    }
}

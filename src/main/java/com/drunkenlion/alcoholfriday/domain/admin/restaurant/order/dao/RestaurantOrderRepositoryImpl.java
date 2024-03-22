package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrder.restaurantOrder;
import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrderDetail.restaurantOrderDetail;
import static com.drunkenlion.alcoholfriday.domain.member.entity.QMember.member;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurant.restaurant;

@RequiredArgsConstructor
public class RestaurantOrderRepositoryImpl implements RestaurantOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<RestaurantOrder> findRestaurantOrdersByOwner(Member ownerMember, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(restaurantOrder.member.eq(ownerMember))
                .and(restaurantOrder.deletedAt.isNull());

        List<RestaurantOrder> restaurantOrders = jpaQueryFactory
                .select(restaurantOrder)
                .from(restaurantOrder)
                .leftJoin(restaurantOrder.member, member).fetchJoin()
                .leftJoin(restaurantOrder.restaurant, restaurant).fetchJoin()
                .leftJoin(restaurantOrder.restaurantOrderDetails, restaurantOrderDetail).fetchJoin()
                .leftJoin(restaurantOrderDetail.product, product).fetchJoin()
                .where(booleanBuilder)
                .orderBy(restaurantOrder.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurantOrder.count())
                .from(restaurantOrder)
                .leftJoin(restaurantOrder.member, member)
                .leftJoin(restaurantOrder.restaurant, restaurant)
                .leftJoin(restaurantOrder.restaurantOrderDetails, restaurantOrderDetail)
                .leftJoin(restaurantOrderDetail.product, product)
                .where(booleanBuilder);

        return PageableExecutionUtils.getPage(restaurantOrders, pageable, total::fetchOne);
    }
}

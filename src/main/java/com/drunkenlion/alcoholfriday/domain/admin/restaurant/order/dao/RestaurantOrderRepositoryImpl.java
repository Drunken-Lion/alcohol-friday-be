package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrder.restaurantOrder;
import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.QRestaurantOrderDetail.restaurantOrderDetail;
import static com.drunkenlion.alcoholfriday.domain.maker.entity.QMaker.maker;
import static com.drunkenlion.alcoholfriday.domain.member.entity.QMember.member;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurant.restaurant;

@RequiredArgsConstructor
public class RestaurantOrderRepositoryImpl implements RestaurantOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<RestaurantOrder> findAllRestaurantOrders(Pageable pageable) {
        List<RestaurantOrder> restaurantOrders = jpaQueryFactory
                .selectFrom(restaurantOrder)
                .leftJoin(restaurantOrder.member, member).fetchJoin()
                .leftJoin(restaurantOrder.restaurant, restaurant).fetchJoin()
                .leftJoin(restaurantOrder.details, restaurantOrderDetail).fetchJoin()
                .leftJoin(restaurantOrderDetail.product, product).fetchJoin()
                .leftJoin(product.maker, maker).fetchJoin()
                .orderBy(restaurantOrder.createdAt.desc(), restaurantOrder.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurantOrder.count())
                .from(restaurantOrder)
                .leftJoin(restaurantOrder.member, member)
                .leftJoin(restaurantOrder.restaurant, restaurant)
                .leftJoin(restaurantOrder.details, restaurantOrderDetail)
                .leftJoin(restaurantOrderDetail.product, product)
                .leftJoin(product.maker, maker);

        return PageableExecutionUtils.getPage(restaurantOrders, pageable, total::fetchOne);
    }

    @Override
    public Page<RestaurantOrder> findRestaurantOrdersByOwner(Member ownerMember, Restaurant ownerRestaurant, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(restaurantOrder.member.eq(ownerMember))
                .and(restaurantOrder.restaurant.eq(ownerRestaurant))
                .and(restaurantOrder.deletedAt.isNull());

        List<RestaurantOrder> restaurantOrders = jpaQueryFactory
                .selectFrom(restaurantOrder)
                .leftJoin(restaurantOrder.member, member).fetchJoin()
                .leftJoin(restaurantOrder.restaurant, restaurant).fetchJoin()
                .leftJoin(restaurantOrder.details, restaurantOrderDetail).fetchJoin()
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
                .leftJoin(restaurantOrder.details, restaurantOrderDetail)
                .leftJoin(restaurantOrderDetail.product, product)
                .where(booleanBuilder);

        return PageableExecutionUtils.getPage(restaurantOrders, pageable, total::fetchOne);
    }

    @Override
    public Optional<RestaurantOrder> findRestaurantOrderAddInfo(Long id) {
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
    public Optional<RestaurantOrder> findRestaurantOrderWaitingApproval(Long id) {
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

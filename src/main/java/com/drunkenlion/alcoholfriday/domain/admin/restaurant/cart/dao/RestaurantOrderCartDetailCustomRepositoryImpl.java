package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.QRestaurantOrderCart.restaurantOrderCart;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurant.restaurant;

import static com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.QRestaurantOrderCartDetail.restaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RestaurantOrderCartDetailCustomRepositoryImpl implements RestaurantOrderCartDetailCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RestaurantOrderCartDetail> findRestaurantAndMember(Restaurant restaurantData, Member memberData) {

        BooleanExpression conditions =
                restaurant.eq(restaurantData)
                        .and(restaurant.member.eq(memberData))
                        .and(restaurantOrderCartDetail.deletedAt.isNull())
                        .and(restaurantOrderCartDetail.quantity.gt(0L));

        return jpaQueryFactory
                .selectFrom(restaurantOrderCartDetail)
                .leftJoin(restaurantOrderCartDetail.restaurantOrderCart, restaurantOrderCart)
                .leftJoin(restaurantOrderCart.restaurant, restaurant)
                .where(conditions)
                .fetch();
    }

    @Override
    public Page<RestaurantOrderCartDetail> findRestaurantAndMember(Restaurant restaurantData, Member memberData, Pageable pageable) {
        BooleanExpression conditions =
                restaurant.eq(restaurantData)
                        .and(restaurant.member.eq(memberData))
                        .and(restaurantOrderCartDetail.deletedAt.isNull())
                        .and(restaurantOrderCartDetail.quantity.gt(0L));

        List<RestaurantOrderCartDetail> cartDetails = jpaQueryFactory
                .selectFrom(restaurantOrderCartDetail)
                .leftJoin(restaurantOrderCartDetail.restaurantOrderCart, restaurantOrderCart)
                .leftJoin(restaurantOrderCart.restaurant, restaurant)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(restaurantOrderCartDetail.createdAt.desc())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurantOrderCartDetail.count())
                .from(restaurantOrderCartDetail)
                .leftJoin(restaurantOrderCartDetail.restaurantOrderCart, restaurantOrderCart)
                .leftJoin(restaurantOrderCart.restaurant, restaurant)
                .where(conditions);

        return PageableExecutionUtils.getPage(cartDetails, pageable, total::fetchOne);
    }

    @Override
    public Optional<RestaurantOrderCartDetail> findCartAndProduct(RestaurantOrderCart cart, Product product) {
        BooleanExpression conditions =
                restaurantOrderCartDetail.restaurantOrderCart.eq(cart)
                        .and(restaurantOrderCartDetail.product.eq(product))
                        .and(restaurantOrderCartDetail.deletedAt.isNull());

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(restaurantOrderCartDetail)
                .where(conditions)
                .fetchOne());
    }
}

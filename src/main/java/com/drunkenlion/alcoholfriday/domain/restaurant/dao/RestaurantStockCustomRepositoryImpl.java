package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.member.entity.QMember.member;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurant.restaurant;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurantStock.restaurantStock;

@RequiredArgsConstructor
public class RestaurantStockCustomRepositoryImpl implements RestaurantStockCustomRepository {
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

    @Override
    public Page<RestaurantStock> findRestaurantStocks(Member authMember, Restaurant findRestaurant, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(restaurantStock.restaurant.eq(findRestaurant))
                .and(restaurantStock.deletedAt.isNull());

        if (authMember.getRole().equals(MemberRole.OWNER)) {
            booleanBuilder.and(restaurantStock.restaurant.member.eq(authMember));
        }

        List<RestaurantStock> stocks = queryFactory
                .selectFrom(restaurantStock)
                .leftJoin(restaurantStock.restaurant, restaurant).fetchJoin()
                .leftJoin(restaurant.member, member).fetchJoin()
                .leftJoin(restaurantStock.product, product).fetchJoin()
                .where(booleanBuilder)
                .orderBy(restaurantStock.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(restaurantStock.count())
                .from(restaurantStock)
                .leftJoin(restaurantStock.restaurant, restaurant)
                .leftJoin(restaurant.member, member)
                .leftJoin(restaurantStock.product, product)
                .where(booleanBuilder);

        return PageableExecutionUtils.getPage(stocks, pageable, total::fetchOne);
    }
}

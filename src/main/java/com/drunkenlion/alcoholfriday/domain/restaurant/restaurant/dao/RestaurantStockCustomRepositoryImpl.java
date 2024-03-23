package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurant.restaurant;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurantStock.restaurantStock;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class RestaurantStockCustomRepositoryImpl implements RestaurantStockCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Optional<RestaurantStock> findRestaurantAndProduct(Restaurant restaurant, Product product) {
        BooleanExpression conditions =
                restaurantStock.restaurant.eq(restaurant)
                        .and(restaurantStock.product.eq(product))
                        .and(restaurantStock.deletedAt.isNull());

        return Optional.ofNullable(query
                .selectFrom(restaurantStock)
                .where(conditions)
                .fetchFirst()
        );
    }


    @Override
    public Page<RestaurantStock> findRestaurantStock(Long id, Pageable pageable) {
        BooleanExpression conditions =
                restaurant.id.eq(id)
                        .and(restaurantStock.deletedAt.isNull())
                        .and(restaurant.deletedAt.isNull());

        List<RestaurantStock> findRestaurantStocks = query
                .select(restaurantStock)
                .from(restaurantStock)
                .leftJoin(restaurantStock.restaurant, restaurant)
                .leftJoin(restaurantStock.product, product)
                .where(conditions)
                .fetch();

        JPAQuery<Long> total = query.select(restaurantStock.count())
                .from(restaurantStock)
                .leftJoin(restaurantStock.restaurant, restaurant)
                .leftJoin(restaurantStock.product, product)
                .where(conditions);

        return PageableExecutionUtils.getPage(findRestaurantStocks, pageable, total::fetchOne);
    }
}

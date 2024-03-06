package com.drunkenlion.alcoholfriday.domain.restaurant.dao;


import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurant.restaurant;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurantStock.restaurantStock;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom{

    private final JPAQueryFactory query;

    @Transactional(readOnly = true)
    public List<Restaurant> getRestaurant(
            double neLatitude, double neLongitude, double swLatitude, double swLongitude) {
        String polygon = String.format("POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                swLongitude, neLatitude,
                neLongitude, neLatitude,
                neLongitude, swLatitude,
                swLongitude, swLatitude,
                swLongitude, neLatitude);

        BooleanExpression inPolygon = Expressions.booleanTemplate(
                "ST_Contains(ST_PolygonFromText({0}), {1})", polygon, restaurant.location);
        BooleanExpression isNotDeleted = restaurant.deletedAt.isNull();

        return query
                .select(restaurant)
                .from(restaurant)
                .leftJoin(restaurant.restaurantStocks, restaurantStock).fetchJoin()
                .leftJoin(restaurantStock.item, item).fetchJoin()
                .where(inPolygon.and(isNotDeleted))
                .fetch();
    }
}

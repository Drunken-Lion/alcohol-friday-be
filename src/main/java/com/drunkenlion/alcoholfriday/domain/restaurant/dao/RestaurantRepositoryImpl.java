package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurant.restaurant;
import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurantStock.restaurantStock;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Restaurant> findAllBasedAuth(Member authMember, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (!authMember.getRole().equals(MemberRole.ADMIN)) {
            builder.and(restaurant.members.id.eq(authMember.getId()));
        }

        List<Restaurant> restaurants = jpaQueryFactory
                .select(restaurant)
                .from(restaurant)
                .where(builder)
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(builder);

        return PageableExecutionUtils.getPage(restaurants, pageable, total::fetchOne);
    }

    @Override
    public List<Restaurant> getRestaurant(double neLatitude, double neLongitude, double swLatitude, double swLongitude) {
        String polygon = String.format("POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                swLongitude, neLatitude,
                neLongitude, neLatitude,
                neLongitude, swLatitude,
                swLongitude, swLatitude,
                swLongitude, neLatitude);

        BooleanExpression inPolygon = Expressions.booleanTemplate(
                "ST_Contains(ST_PolygonFromText({0}), {1})", polygon, restaurant.location);
        BooleanExpression isNotDeleted = restaurant.deletedAt.isNull();

        return jpaQueryFactory
                .select(restaurant)
                .from(restaurant)
                .leftJoin(restaurant.restaurantStocks, restaurantStock).fetchJoin()
                .leftJoin(restaurantStock.product, product).fetchJoin()
                .where(inPolygon.and(isNotDeleted))
                .fetch();
    }
}

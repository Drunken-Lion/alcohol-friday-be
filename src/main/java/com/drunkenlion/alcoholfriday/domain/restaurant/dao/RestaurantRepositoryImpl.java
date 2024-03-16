package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

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
                .offset(pageable.getOffset())
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

    @Override
    public Page<RestaurantNearbyResponse> getRestaurantSellingProducts(double userLocationLatitude, double userLocationLongitude, String keyword, Pageable pageable) {
        BooleanExpression isMeasurement = isWithinDistance(userLocationLongitude, userLocationLatitude, 5000);
        OrderSpecifier<Double> closestStoreDistanceFromUser = getClosestStoreDistanceFromUser(userLocationLatitude, userLocationLongitude);

        List<RestaurantNearbyResponse> restaurants = jpaQueryFactory
                .select(Projections.constructor(
                        RestaurantNearbyResponse.class,
                        restaurant.id,
                        restaurant.members.id,
                        restaurant.name,
                        restaurant.location,
                        restaurant.address,
                        product.name,
                        Expressions.numberTemplate(Double.class, "ST_Distance_Sphere(point({0}, {1}), restaurant.location)", userLocationLongitude, userLocationLatitude).as("distance")
                        ))
                .from(restaurant)
                .leftJoin(restaurant.restaurantStocks, restaurantStock)
                .leftJoin(restaurantStock.product, product)
                .where(isMeasurement
                        .and(isProductName(keyword))
                        .and(isNullDeleted())
                        .and(isNotQuantity())
                ).orderBy(closestStoreDistanceFromUser)
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant);

        return PageableExecutionUtils.getPage(restaurants, pageable, total::fetchOne);
    }
    public BooleanExpression isProductName(String keyword) {
        return !StringUtils.hasText(keyword) ? null : product.name.eq(keyword);
    }
    public OrderSpecifier<Double> getClosestStoreDistanceFromUser(double userLocationLatitude, double userLocationLongitude) {
        return Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), restaurant.location)",
                userLocationLongitude, userLocationLatitude).asc();
    }
    public BooleanExpression isWithinDistance(double userLocationLongitude, double userLocationLatitude, double radius) {
        return Expressions.booleanTemplate(
                "ST_Distance_Sphere(point({0}, {1}), restaurant.location) <= {2}",
                userLocationLongitude, userLocationLatitude, radius);
    }
    public BooleanExpression isNotQuantity() {
        return product.quantity.isNotNull().and(product.quantity.gt(0));
    }
    public BooleanExpression isNullDeleted() {
        return restaurant.deletedAt.isNull();
    }
}

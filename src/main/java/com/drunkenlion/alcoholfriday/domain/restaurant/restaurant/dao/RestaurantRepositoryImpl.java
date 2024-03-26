package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
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

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurant.restaurant;
import static com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.QRestaurantStock.restaurantStock;


@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Restaurant> findAllBasedAuth(Member authMember, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (!authMember.getRole().equals(MemberRole.ADMIN)) {
            builder.and(restaurant.member.id.eq(authMember.getId()));
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
    public List<Restaurant> getRestaurant(double neLatitude, double neLongitude, double swLatitude,
                                          double swLongitude) {
        String polygon = String.format("POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                swLongitude, neLatitude,
                neLongitude, neLatitude,
                neLongitude, swLatitude,
                swLongitude, swLatitude,
                swLongitude, neLatitude);

        BooleanExpression inPolygon = Expressions.booleanTemplate("ST_Contains(ST_PolygonFromText({0}), {1})", polygon, restaurant.location);

        return jpaQueryFactory
                .select(restaurant)
                .from(restaurant)
                .leftJoin(restaurant.restaurantStocks, restaurantStock).fetchJoin()
                .leftJoin(restaurantStock.product, product).fetchJoin()
                .where(inPolygon.and(restaurant.deletedAt.isNull()))
                .fetch();
    }

    @Override
    public Page<RestaurantNearbyResponse> getRestaurantSellingProducts(double userLocationLatitude,
                                                                       double userLocationLongitude, Item item,
                                                                       Pageable pageable) {
        BooleanExpression isMeasurement = isRestaurantWithinRadius(userLocationLongitude, userLocationLatitude, 5000);
        OrderSpecifier<Double> closestStoreDistanceFromUser = getClosestStoreDistanceFromUser(userLocationLatitude,
                userLocationLongitude);
        BooleanBuilder itemConditions = new BooleanBuilder();

        for (ItemProduct itemProduct : item.getItemProducts()) {
            itemConditions.or(product.name.eq(itemProduct.getProduct().getName()));
        }

        List<RestaurantNearbyResponse> restaurants = jpaQueryFactory
                .select(Projections.constructor(
                        RestaurantNearbyResponse.class,
                        restaurant.id,
                        restaurant.name,
                        restaurant.address,
                        product.name,
                        Expressions.numberTemplate(Double.class, "ST_Distance_Sphere(point({0}, {1}), restaurant.location)", userLocationLongitude, userLocationLatitude)
                ))
                .from(restaurant)
                .leftJoin(restaurant.restaurantStocks, restaurantStock)
                .leftJoin(restaurantStock.product, product)
                .where(isMeasurement
                        .and(itemConditions)
                        .and(restaurant.deletedAt.isNull())
                        .and(restaurantStock.quantity.isNotNull())
                        .or(restaurantStock.quantity.loe(0))
                ).orderBy(closestStoreDistanceFromUser)
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(isMeasurement
                        .and(itemConditions)
                        .and(restaurant.deletedAt.isNull())
                        .and(restaurantStock.quantity.isNotNull())
                        .or(restaurantStock.quantity.loe(0))
                );

        return PageableExecutionUtils.getPage(restaurants, pageable, total::fetchOne);
    }

    private OrderSpecifier<Double> getClosestStoreDistanceFromUser(double userLocationLatitude, double userLocationLongitude) {
        return Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), restaurant.location)",
                userLocationLongitude, userLocationLatitude).asc();
    }

    private BooleanExpression isRestaurantWithinRadius(double userLocationLongitude, double userLocationLatitude, double radius) {
        return Expressions.booleanTemplate(
                "ST_Distance_Sphere(point({0}, {1}), restaurant.location) <= {2}",
                userLocationLongitude, userLocationLatitude, radius);
    }
}

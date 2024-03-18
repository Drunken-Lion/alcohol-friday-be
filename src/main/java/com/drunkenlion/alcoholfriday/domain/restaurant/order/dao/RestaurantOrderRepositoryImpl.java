package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantOrderRepositoryImpl implements RestaurantOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
}

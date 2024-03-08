package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.restaurant.entity.QRestaurant.restaurant;

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
}

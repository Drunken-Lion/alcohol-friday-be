package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrder.order;
import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrderDetail.orderDetail;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findMyOrderList(Member member, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.member.id.eq(member.getId()))
                .and(order.deletedAt.isNull());

        List<Order> orders = jpaQueryFactory
                .select(order)
                .from(order)
                .leftJoin(order.member).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.item, item).fetchJoin()
                .where(builder)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(order.count())
                .from(order)
                .leftJoin(order.member)
                .leftJoin(order.orderDetails)
                .where(builder);

        return PageableExecutionUtils.getPage(orders, pageable, total::fetchOne);
    }
}

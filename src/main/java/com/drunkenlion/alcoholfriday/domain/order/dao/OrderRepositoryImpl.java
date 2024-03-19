package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.QOrder;
import com.drunkenlion.alcoholfriday.domain.payment.entity.QPayment;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrder.order;
import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
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
                .leftJoin(order.orderDetails, orderDetail)
                .leftJoin(orderDetail.item, item)
                .where(builder);

        return PageableExecutionUtils.getPage(orders, pageable, total::fetchOne);
    }

    @Override
    public Page<OrderListResponse> findOrderList(Pageable pageable, OrderStatus status) {
        BooleanBuilder builder = new BooleanBuilder();

        QOrder o = new QOrder("o");
        QPayment p = new QPayment("p");
        QPayment p2 = new QPayment("p2");

        if (status != null) {
            builder.and(o.orderStatus.eq(status));
        }

        List<OrderListResponse> orders = jpaQueryFactory
                .select(Projections.constructor(
                        OrderListResponse.class,
                        o,
                        p.issuerCode
                ))
                .from(o)
                .leftJoin(p).on(p.order.eq(o))
                .leftJoin(p2).on(p2.order.eq(p.order).and(p.id.lt(p2.id)))
                .where(p2.id.isNull())
                .where(builder)
                .orderBy(o.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(o.count())
                .from(o)
                .where(builder);

        return PageableExecutionUtils.getPage(orders, pageable, total::fetchOne);
    }
}

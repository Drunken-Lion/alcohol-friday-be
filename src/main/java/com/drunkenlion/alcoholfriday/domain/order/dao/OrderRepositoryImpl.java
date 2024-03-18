package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.QOrder;
import com.drunkenlion.alcoholfriday.domain.payment.entity.QPayment;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<OrderListResponse> findOrderList(Pageable pageable, OrderStatus status) {
        BooleanBuilder builder = new BooleanBuilder();

        QOrder o = new QOrder("o");
        QPayment p = new QPayment("p");
        QPayment p2 = new QPayment("p2");

        if (status != null) {
            builder.and(order.orderStatus.eq(status));
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
                .select(order.count())
                .from(order)
                .where(builder);

        return PageableExecutionUtils.getPage(orders, pageable, total::fetchOne);
    }
}

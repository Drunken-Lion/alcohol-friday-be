package com.drunkenlion.alcoholfriday.domain.order.dao;

import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrder.order;
import static com.drunkenlion.alcoholfriday.domain.order.entity.QOrderDetail.orderDetail;
import static com.drunkenlion.alcoholfriday.domain.review.entity.QReview.review;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class OrderDetailCustomRepositoryImpl implements OrderDetailCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<OrderDetail> findOrderDetailsMember(Member member, Pageable pageable) {
        BooleanExpression conditions =
                orderDetail.order.member.eq(member)
                        .and(orderDetail.order.orderStatus.eq(OrderStatus.DELIVERED))
                        .and(orderDetail.order.deletedAt.isNull())
                        .and(review.isNull().or(review.deletedAt.isNotNull()));

        List<OrderDetail> orderDetails = jpaQueryFactory
                .selectFrom(orderDetail)
                .leftJoin(orderDetail.order, order)
                .leftJoin(orderDetail.review, review)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderDetail.createdAt.desc())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(orderDetail.count())
                .from(orderDetail)
                .leftJoin(orderDetail.order, order)
                .leftJoin(orderDetail.review, review)
                .where(conditions);

        return PageableExecutionUtils.getPage(orderDetails, pageable, total::fetchOne);
    }
}

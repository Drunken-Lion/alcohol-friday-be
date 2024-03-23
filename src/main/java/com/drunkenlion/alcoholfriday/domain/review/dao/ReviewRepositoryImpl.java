package com.drunkenlion.alcoholfriday.domain.review.dao;

import static com.drunkenlion.alcoholfriday.domain.review.entity.QReview.review;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<Review> findItemDetailReview(Item item, Pageable pageable) {
        BooleanExpression conditions =
                review.item.eq(item)
                        .and(review.deletedAt.isNull());

        List<Review> reviews = query.selectFrom(review)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = query.select(review.count())
                .from(review)
                .where(conditions);

        return PageableExecutionUtils.getPage(reviews, pageable, total::fetchOne);
    }
}

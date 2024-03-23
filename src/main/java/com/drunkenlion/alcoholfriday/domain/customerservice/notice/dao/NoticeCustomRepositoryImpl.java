package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import static com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.QNotice.notice;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NoticeCustomRepositoryImpl implements NoticeCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Notice> findNotices(Pageable pageable, String keyword, List<String> keywordType) {
        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression basicConditions = notice.status.eq(NoticeStatus.PUBLISHED)
                                            .and(notice.deletedAt.isNull());
                                            builder.and(basicConditions);

        return getNotices(pageable, keyword, keywordType, builder);
    }

    @Override
    public Page<Notice> findAllNotices(Pageable pageable, String keyword, List<String> keywordType) {
        BooleanBuilder builder = new BooleanBuilder();

        return getNotices(pageable, keyword, keywordType, builder);
    }

    private Page<Notice> getNotices(Pageable pageable, String keyword, List<String> keywordType, BooleanBuilder builder) {
        if (!keyword.isBlank()) {
            List<BooleanExpression> conditions = new ArrayList<>();

            if (keywordType.contains("title")) conditions.add(notice.title.contains(keyword));
            if (keywordType.contains("content")) conditions.add(notice.content.contains(keyword));

            conditions.stream()
                    .reduce(BooleanExpression::or)
                    .ifPresent(builder::and);
        }

        List<Notice> fetch = jpaQueryFactory // 데이터 조회 쿼리
                .selectFrom(notice)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory // 전체 개수 조회 쿼리
                .select(notice.count())
                .from(notice)
                .where(builder);

        return PageableExecutionUtils.getPage(fetch, pageable, total::fetchOne);
    }
}

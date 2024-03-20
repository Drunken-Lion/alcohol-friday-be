package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import static com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.QNotice.notice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class NoticeCustomRepositoryImpl implements NoticeCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Notice> findNotices(Pageable pageable) {
        BooleanExpression conditions =
                notice.status.eq(NoticeStatus.PUBLISHED)
                        .and(notice.deletedAt.isNull()); //where 조건

        List<Notice> fetch = jpaQueryFactory
                .selectFrom(notice)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> where = jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .where(conditions);

        return PageableExecutionUtils.getPage(fetch, pageable, where::fetchOne);
    }

    @Override
    public Optional<Notice> findNotice(Long id) {
        BooleanExpression conditions =
                notice.status.eq(NoticeStatus.PUBLISHED)
                        .and(notice.deletedAt.isNull())
                        .and(notice.id.eq(id));

        return Optional.of(jpaQueryFactory
                .selectFrom(notice)
                .where(conditions)
                .fetchFirst());
    }
}

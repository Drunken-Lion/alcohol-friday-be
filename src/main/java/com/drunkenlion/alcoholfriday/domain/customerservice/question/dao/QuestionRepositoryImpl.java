package com.drunkenlion.alcoholfriday.domain.customerservice.question.dao;

import static com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.QAnswer.answer;
import static com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.QQuestion.question;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Question> findMember(Member member, Pageable pageable) {
        BooleanExpression memberAndNotDeletedExpression =
                question.member.eq(member)
                        .and(question.deletedAt.isNull());

        List<Question> questions = jpaQueryFactory
                .selectFrom(question)
                .where(memberAndNotDeletedExpression)
                .leftJoin(question).on(answer.question.id.eq(question.id)).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(question.count())
                .from(question)
                .where(memberAndNotDeletedExpression);

        return PageableExecutionUtils.getPage(questions, pageable, total::fetchOne);
    }

    @Override
    public Optional<Question> findQuestion(Long id) {
        BooleanExpression conditions =
                question.id.eq(id)
                .and(question.deletedAt.isNull())
                .and(answer.deletedAt.isNull());

        return Optional.of(jpaQueryFactory
                .selectFrom(question)
                .leftJoin(question.answers, answer).fetchJoin()
                .where(conditions)
                .fetchFirst());
    }

    @Override
    public Optional<Question> adminFindQuestion(Long id) {
        BooleanExpression conditions =
                question.id.eq(id)
                        .and(answer.deletedAt.isNull());

        return Optional.of(jpaQueryFactory
                .selectFrom(question)
                .leftJoin(question.answers, answer).fetchJoin()
                .where(conditions)
                .fetchFirst());
    }
}

package com.drunkenlion.alcoholfriday.domain.customerservice.dao;

import static com.drunkenlion.alcoholfriday.domain.customerservice.entity.QAnswer.answer;
import static com.drunkenlion.alcoholfriday.domain.customerservice.entity.QQuestion.question;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
}

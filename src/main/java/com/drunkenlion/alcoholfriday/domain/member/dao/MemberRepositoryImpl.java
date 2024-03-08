package com.drunkenlion.alcoholfriday.domain.member.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Member> findAllBasedAuth(Member authMember, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (authMember.getRole().equals(MemberRole.SUPER_VISOR)) {
            // member.role이 ADMIM이 아닌 경우
            builder.and(member.role.ne(MemberRole.ADMIN));
            // member.role이 SUPER_VISOR가 아니거나 member.id가 authMember.id와 같은 경우
            builder.and(member.role.ne(MemberRole.SUPER_VISOR).or(member.id.eq(authMember.getId())));
        }

        List<Member> members = jpaQueryFactory
                .select(member)
                .from(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(builder);

        return PageableExecutionUtils.getPage(members, pageable, total::fetchOne);
    }
}

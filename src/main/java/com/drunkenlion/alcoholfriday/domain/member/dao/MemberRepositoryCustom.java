package com.drunkenlion.alcoholfriday.domain.member.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findAllBasedAuth(Member member, Pageable pageable);
}

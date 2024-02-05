package com.drunkenlion.alcoholfriday.domain.member.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

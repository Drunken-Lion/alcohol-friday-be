package com.drunkenlion.alcoholfriday.domain.member.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}

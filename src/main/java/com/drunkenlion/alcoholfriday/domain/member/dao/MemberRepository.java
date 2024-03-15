package com.drunkenlion.alcoholfriday.domain.member.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    Optional<Member> findByName(String name);
}

package com.drunkenlion.alcoholfriday.domain.member.application;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public MemberResponse getMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다."));

        return MemberResponse.of(member);
    }
}

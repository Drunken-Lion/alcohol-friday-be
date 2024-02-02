package com.drunkenlion.alcoholfriday.domain.admin.application;

import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{
    private final MemberRepository memberRepository;

    public Page<MemberListResponse> getMembers() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Member> members = memberRepository.findAll(pageable);

        return members.map(MemberListResponse::of);
    }
}

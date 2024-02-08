package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
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

    public Page<MemberListResponse> getMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = memberRepository.findAll(pageable);

        return members.map(MemberListResponse::of);
    }

    public MemberDetailResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        return MemberDetailResponse.of(member);
    }

    @Transactional
    public MemberDetailResponse modifyMember(Long id, MemberModifyRequest memberModifyRequest) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        member = member.toBuilder()
                .nickname(memberModifyRequest.getNickname())
                .role(memberModifyRequest.getRole())
                .phone(memberModifyRequest.getPhone())
                .build();

        memberRepository.save(member);

        return MemberDetailResponse.of(member);
    }
}
package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
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

    @Override
    public Page<MemberListResponse> getMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = memberRepository.findAll(pageable);

        return members.map(MemberListResponse::of);
    }

    @Override
    public MemberDetailResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        return MemberDetailResponse.of(member);
    }

    @Override
    @Transactional
    public MemberDetailResponse modifyMember(Long id, MemberModifyRequest memberModifyRequest) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        member = member.toBuilder()
                .nickname(memberModifyRequest.getNickname())
                .role(memberModifyRequest.getRole())
                .phone(memberModifyRequest.getPhone())
                .build();

        memberRepository.save(member);

        return MemberDetailResponse.of(member);
    }
}

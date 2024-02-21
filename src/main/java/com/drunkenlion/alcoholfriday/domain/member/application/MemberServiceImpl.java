package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
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

    @Transactional
    @Override
    public MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest) {
        if (memberRepository.existsByNickname(modifyRequest.getNickname())) {
            throw new BusinessException(HttpResponse.Fail.NICKNAME_IN_USE);
        }

        member = member.toBuilder()
                .nickname(modifyRequest.getNickname())
                .phone(modifyRequest.getPhone())
                .build();

        return MemberResponse.of(memberRepository.save(member));
    }
}

package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final QuestionRepository questionRepository;

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

    @Override
    public PageResponse<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findByMember_IdOrderByCreatedAtDesc(memberId, pageable);

        Page<MemberQuestionListResponse> memberQuestionListResponsePage =
                questionPage.map(MemberQuestionListResponse::of);

        return PageResponse.of(memberQuestionListResponsePage);
    }
}

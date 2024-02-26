package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface MemberService {
    MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest);

    Page<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size);
}

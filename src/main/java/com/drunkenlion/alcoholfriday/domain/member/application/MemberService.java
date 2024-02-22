package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;

public interface MemberService {
    MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest);

    PageResponse<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size);
}

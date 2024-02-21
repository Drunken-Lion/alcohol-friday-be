package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface MemberService {
    MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest);
}

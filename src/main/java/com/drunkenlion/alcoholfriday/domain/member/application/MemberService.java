package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;

public interface MemberService {
    MemberResponse getMember(String email);

    MemberResponse modifyMember(String email, MemberModifyRequest modifyRequest);
}

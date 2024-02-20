package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface MemberService {
    MemberResponse getMember(String email);

    // TODO 일단 보류
    Member findMember(String email);
}

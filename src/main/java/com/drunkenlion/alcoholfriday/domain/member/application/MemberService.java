package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;

public interface MemberService {
    MemberResponse getMember(String email);
}

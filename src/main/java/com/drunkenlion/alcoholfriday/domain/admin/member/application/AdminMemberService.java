package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import org.springframework.data.domain.Page;

public interface AdminMemberService {
    Page<MemberListResponse> getMembers(int page, int size);

    MemberDetailResponse getMember(Long id);

    MemberDetailResponse modifyMember(Long id, MemberModifyRequest memberModifyRequest);
}

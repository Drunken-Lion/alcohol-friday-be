package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminMemberService {
    Page<MemberListResponse> getMembers(Member member, int page, int size);
    MemberDetailResponse getMember(Member member, Long id);
    MemberDetailResponse modifyMember(Member member, Long id, MemberModifyRequest memberModifyRequest);

}

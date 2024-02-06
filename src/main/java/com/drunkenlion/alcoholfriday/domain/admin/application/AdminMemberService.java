package com.drunkenlion.alcoholfriday.domain.admin.application;

import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import org.springframework.data.domain.Page;

public interface AdminMemberService {
    Page<MemberListResponse> getMembers(int page, int size);

    MemberDetailResponse getMember(Long id);
}

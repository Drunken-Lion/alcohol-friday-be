package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminNoticeService {
    NoticeSaveResponse getNotice(Long id, Member member);

    Page<NoticeSaveResponse> getNotices(int page, int size, Member member);

    NoticeSaveResponse saveNotice(NoticeSaveRequest request, Member member);

    NoticeSaveResponse modifyNotice(Long id, NoticeSaveRequest request, Member member);

    NoticeSaveResponse deleteNotice(Long id, Member member);
}
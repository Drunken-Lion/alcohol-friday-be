package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminNoticeService {
    NoticeSaveResponse getNotice(Long id, Member member);

    Page<NoticeSaveResponse> getNotices(int page, int size, Member member, String keyword, List<String> keywordType);

    NoticeSaveResponse initNotice(Member member);
    
    NoticeSaveResponse modifyNotice(Long id, NoticeSaveRequest request, Member member);

    void deleteNotice(Long id, Member member);
}

package com.drunkenlion.alcoholfriday.domain.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeDetailResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeListResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NoticeService {

    NoticeDetailResponse getNotice(Long id);

    Page<NoticeListResponse> getNotices(int page, int size, String keyword, List<String> keywordType);
}

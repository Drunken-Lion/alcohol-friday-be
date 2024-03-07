package com.drunkenlion.alcoholfriday.domain.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeDetailResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeListResponse;
import org.springframework.data.domain.Page;

public interface NoticeService {

    NoticeDetailResponse getNotice(Long id);
}

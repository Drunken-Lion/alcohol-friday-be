package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeCustomRepository {
    Page<Notice> findNotices(Pageable pageable, String keyword, List<String> keywordType);

    Page<Notice> findAllNotices(Pageable pageable, String keyword, List<String> keywordType);
}

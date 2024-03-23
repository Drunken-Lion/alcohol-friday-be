package com.drunkenlion.alcoholfriday.domain.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeDetailResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeListResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;

    @Override
    public NoticeDetailResponse getNotice(Long id) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE));
        if(notice.getStatus() == NoticeStatus.DRAFT) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE);
        }

        return NoticeDetailResponse.of(notice);
    }

    @Override
    public Page<NoticeListResponse> getNotices(int page, int size, String keyword, List<String> keywordType) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository.findNotices(pageable, keyword, keywordType);

        return NoticeListResponse.of(notices);
    }
}

package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeSaveResponse getNotice(Long id, Member member) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE));

        return NoticeSaveResponse.of(notice);
    }

    @Override
    public Page<NoticeSaveResponse> getNotices(int page, int size, Member member, String keyword, List<String> keywordType) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository.findAllNotices(pageable, keyword, keywordType);

        return NoticeSaveResponse.of(notices);
    }

    @Override
    @Transactional
    public NoticeSaveResponse initNotice(Member member) {
        Notice notice = Notice.builder()
                .member(member)
                .status(NoticeStatus.DRAFT)
                .build();
        noticeRepository.save(notice);

        return NoticeSaveResponse.of(notice);
    }

    @Override
    @Transactional
    public NoticeSaveResponse modifyNotice(Long id, NoticeSaveRequest request, Member member) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE));

        notice.updateNotice(request.getTitle(), request.getContent());

        noticeRepository.save(notice);

        return NoticeSaveResponse.of(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long id, Member member) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE));

        notice = notice.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);
    }
}

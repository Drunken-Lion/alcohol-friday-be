package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
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

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeSaveResponse getNotice(Long id, Member member) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_NOTICE)
                        .build());

        return NoticeSaveResponse.of(notice);
    }

    @Override
    public Page<NoticeSaveResponse> getNotices(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository.findAll(pageable);

        return notices.map(NoticeSaveResponse::of);
    }

    @Override
    @Transactional
    public NoticeSaveResponse saveNotice(NoticeSaveRequest request, Member member) {
        Notice notice = noticeRepository.save(NoticeSaveRequest.toEntity(request, member));

        return NoticeSaveResponse.of(notice);
    }

    @Override
    @Transactional
    public NoticeSaveResponse modifyNotice(Long id, NoticeSaveRequest request, Member member) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_NOTICE)
                        .build());

        notice = notice.toBuilder()
                .title(request.getTitle())
                .content(request.getContent())
                .updatedAt(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);

        return NoticeSaveResponse.of(notice);
    }

    @Override
    @Transactional
    public NoticeSaveResponse deleteNotice(Long id, Member member) {
        // 없는 번호를 삭제하려 하면
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_NOTICE)
                        .build());
        // 삭제된 게시물을 다시 삭제하려 시도하면
        if (notice.getDeletedAt() != null) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.NOT_FOUND_NOTICE)
                    .build();
        }

        notice = notice.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);

        return NoticeSaveResponse.of(notice);
    }
}

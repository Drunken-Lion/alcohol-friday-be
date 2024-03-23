package com.drunkenlion.alcoholfriday.domain.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeDetailResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class NoticeServiceTest {
    @InjectMocks
    private NoticeServiceImpl noticeService;
    @Mock
    private NoticeRepository noticeRepository;

    private final Long noticeId = 1L;
    private final String title = "test title 1";
    private final String content = "test content 1";
    private final LocalDateTime noticeCreatedAt = LocalDateTime.now();
    private final LocalDateTime noticeUpdatedAt = noticeCreatedAt.plusMinutes(10);
    private final LocalDateTime noticeDeletedAt = null;

    private final int page = 0;
    private final int size = 20;

    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final Long phone = 1012345678L;
    private final LocalDateTime memberCreatedAt = LocalDateTime.now();
    private final LocalDateTime memberUpdatedAt = null;
    private final LocalDateTime memberDeletedAt = null;

    @DisplayName("공지사항 목록 조회 성공")
    @Test
    public void getNoticesTest() {
        when(this.noticeRepository.findNotices(any(Pageable.class), any(), any())).thenReturn(this.getNotices());

        Page<NoticeListResponse> notices = noticeService.getNotices(page, size, null, null);

        List<NoticeListResponse> noticeResponse = notices.getContent();
        assertThat(noticeResponse).isInstanceOf(List.class);
        assertThat(noticeResponse.get(0).getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.get(0).getTitle()).isEqualTo(title);
        assertThat(noticeResponse.get(0).getCreatedAt()).isEqualTo(noticeCreatedAt);
        assertThat(noticeResponse.get(0).getUpdatedAt()).isEqualTo(noticeUpdatedAt);
    }

    @DisplayName("공지사항 목록 검색 성공")
    @Test
    public void getNoticesSearchTest() {
        when(this.noticeRepository.findNotices(any(Pageable.class), any(), any())).thenReturn(this.getNotices());

        List<String> keywordType = new ArrayList<>();
        keywordType.add("title");
        keywordType.add("content");
        String keyword = "test";

        Page<NoticeListResponse> notices = noticeService.getNotices(page, size, keyword, keywordType);

        List<NoticeListResponse> noticeResponse = notices.getContent();
        assertThat(noticeResponse).isInstanceOf(List.class);
        assertThat(noticeResponse.get(0).getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.get(0).getTitle()).contains(keyword);
        assertThat(noticeResponse.get(0).getCreatedAt()).isEqualTo(noticeCreatedAt);
        assertThat(noticeResponse.get(0).getUpdatedAt()).isEqualTo(noticeUpdatedAt);
        assertThat(keywordType.get(0)).isEqualTo("title");
    }

    @DisplayName("공지사항 상세 조회 성공")
    @Test
    public void getNoticeTest() {
        when(this.noticeRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getNoticeOne());

        NoticeDetailResponse noticeResponse = noticeService.getNotice(noticeId);

        assertThat(noticeResponse.getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.getTitle()).isEqualTo(title);
        assertThat(noticeResponse.getContent()).isEqualTo(content);
        assertThat(noticeResponse.getCreatedAt()).isEqualTo(noticeCreatedAt);
        assertThat(noticeResponse.getUpdatedAt()).isEqualTo(noticeUpdatedAt);

        assertThat(noticeResponse.getMemberId()).isEqualTo(memberId);
        assertThat(noticeResponse.getMemberName()).isEqualTo(name);
        assertThat(noticeResponse.getMemberNickname()).isEqualTo(nickname);
    }

    @DisplayName("공지사항 상세 조회 실패 - 찾을 수 없는 공지사항")
    @Test
    public void getNoticeNotFoundTest() {
        // given
        // 메서드 모의
        Mockito.when(noticeRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // When 실행
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            noticeService.getNotice(noticeId);
        });

        // then
        // (예상값, 발생한 상태 코드값)
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getMessage(), exception.getMessage());
    }

    private Page<Notice> getNotices() {
        List<Notice> list = List.of(this.getNoticesData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Notice>(list, pageable, list.size());
    }

    private Notice getNoticesData() {

        return Notice.builder()
                .id(noticeId)
                .title(title)
                .createdAt(noticeCreatedAt)
                .updatedAt(noticeUpdatedAt)
                .deletedAt(noticeDeletedAt)
                .build();
    }

    private Optional<Notice> getNoticeOne() {
        return Optional.of(this.getNoticeData());
    }

    private Notice getNoticeData() {
        Member member = getMemberData();

        return Notice.builder()
                .id(noticeId)
                .member(member)
                .title(title)
                .content(content)
                .createdAt(noticeCreatedAt)
                .updatedAt(noticeUpdatedAt)
                .deletedAt(noticeDeletedAt)
                .build();
    }

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(email)
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .createdAt(memberCreatedAt)
                .updatedAt(memberUpdatedAt)
                .deletedAt(memberDeletedAt)
                .build();
    }
}
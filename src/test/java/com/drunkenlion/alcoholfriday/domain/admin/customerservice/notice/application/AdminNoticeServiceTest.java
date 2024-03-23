package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminNoticeServiceTest {
    @InjectMocks
    private AdminNoticeServiceImpl adminNoticeService;
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
    private final String name = "테스트";
    private final String nickname = "test";

    private final String modifyTitle = "test title update";
    private final String modifyContent = "test content update";

    @DisplayName("관리자 공지사항 목록 조회 성공")
    @Test
    public void getAdminNoticesTest() {
        // given
        when(this.noticeRepository.findAllNotices(any(Pageable.class), any(), any())).thenReturn(this.getNotices());

        // when
        Page<NoticeSaveResponse> notices = this.adminNoticeService.getNotices(page, size, getMemberData(), null, null);

        // then
        List<NoticeSaveResponse> noticeResponse = notices.getContent();
        assertThat(noticeResponse).isInstanceOf(List.class);
        assertThat(noticeResponse.get(0).getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.get(0).getTitle()).isEqualTo(title);
        assertThat(noticeResponse.get(0).getContent()).isEqualTo(content);
        assertThat(noticeResponse.get(0).getCreatedAt()).isEqualTo(noticeCreatedAt);

        assertThat(noticeResponse.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(noticeResponse.get(0).getMemberName()).isEqualTo(name);
        assertThat(noticeResponse.get(0).getMemberNickname()).isEqualTo(nickname);
    }

    @DisplayName("관리자 공지사항 목록 검색 성공")
    @Test
    public void getAdminNoticesSearchTest() {
        // given
        when(this.noticeRepository.findAllNotices(any(Pageable.class), any(), any())).thenReturn(this.getNotices());

        List<String> keywordType = new ArrayList<>();
        keywordType.add("title");
        keywordType.add("content");
        String keyword = "test";

        // when
        Page<NoticeSaveResponse> notices = this.adminNoticeService.getNotices(page, size, getMemberData(), keyword, keywordType);

        // then
        List<NoticeSaveResponse> noticeResponse = notices.getContent();
        assertThat(noticeResponse).isInstanceOf(List.class);
        assertThat(noticeResponse.get(0).getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.get(0).getTitle()).isEqualTo(title);
        assertThat(noticeResponse.get(0).getContent()).isEqualTo(content);
        assertThat(noticeResponse.get(0).getCreatedAt()).isEqualTo(noticeCreatedAt);
        assertThat(noticeResponse.get(0).getTitle()).contains(keyword);

        assertThat(noticeResponse.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(noticeResponse.get(0).getMemberName()).isEqualTo(name);
        assertThat(noticeResponse.get(0).getMemberNickname()).isEqualTo(nickname);
    }

    @DisplayName("관리자 공지사항 상세 조회 성공")
    @Test
    public void getAdminNoticeTest() {
        // given
        when(this.noticeRepository.findById(any())).thenReturn(this.getNoticeOne());

        // when
        NoticeSaveResponse noticeResponse = this.adminNoticeService.getNotice(noticeId, getMemberData());

        // then
        assertThat(noticeResponse.getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.getTitle()).isEqualTo(title);
        assertThat(noticeResponse.getContent()).isEqualTo(content);
        assertThat(noticeResponse.getCreatedAt()).isEqualTo(noticeCreatedAt);

        assertThat(noticeResponse.getMemberId()).isEqualTo(memberId);
        assertThat(noticeResponse.getMemberName()).isEqualTo(name);
        assertThat(noticeResponse.getMemberNickname()).isEqualTo(nickname);
    }

    @DisplayName("관리자 공지사항 상세 조회 실패 - 찾을 수 없는 공지사항")
    @Test
    public void getAdminNoticeNotFoundTest() {
        // given
        // 메서드 모의
        Mockito.when(noticeRepository.findById(any())).thenReturn(Optional.empty());

        // When 실행
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminNoticeService.getNotice(noticeId, getMemberData());
        });

        // then
        // (예상값, 발생한 상태 코드값)
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getMessage(), exception.getMessage());
    }

    @DisplayName("관리자 공지사항 수정 성공")
    @Test
    public void updateAdminNoticeTest() {
        // given 준비
        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title(modifyTitle)
                .content(modifyContent)
                .build();
        // 메서드 모의
        // Mock 객체를 정의하는 테스트의 준비 과정, 무엇을 호출하면 어떻게 반환할 지 미리 정의
        Mockito.when(noticeRepository.findByIdAndDeletedAtIsNull(noticeId)).thenReturn(this.getNoticeOne());
        Mockito.when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When 실행
        NoticeSaveResponse noticeResponse = adminNoticeService.modifyNotice(noticeId, request, getMemberData());

        // then 검증
        assertThat(noticeResponse.getId()).isEqualTo(noticeId);
        assertThat(noticeResponse.getTitle()).isEqualTo(modifyTitle);
        assertThat(noticeResponse.getContent()).isEqualTo(modifyContent);
    }

    @DisplayName("관리자 공지사항 수정 실패 - 찾을 수 없는 공지사항")
    @Test
    public void updateAdminNoticeNotFoundTest() {
        // given 준비
        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title(modifyTitle)
                .content(modifyContent)
                .build();

        // 메서드 모의
        when(noticeRepository.findByIdAndDeletedAtIsNull(noticeId)).thenReturn(Optional.empty());

        // When 실행
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminNoticeService.modifyNotice(noticeId, request, getMemberData());
        });
        // then
        // (예상값, 발생한 상태 코드값)
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getMessage(), exception.getMessage());
    }

    @DisplayName("관리자 공지사항 삭제 성공")
    @Test
    public void deleteAdminNoticeTest() {
        // given 준비
        when(noticeRepository.findByIdAndDeletedAtIsNull(noticeId)).thenReturn(this.getNoticeOne());
        ArgumentCaptor<Notice> noticeArgumentCaptor = ArgumentCaptor.forClass(Notice.class);

        // When 실행
        adminNoticeService.deleteNotice(noticeId, getMemberData());

        // then 검증
        verify(noticeRepository).save(noticeArgumentCaptor.capture());
        Notice savedNotice = noticeArgumentCaptor.getValue();
        assertThat(savedNotice.getDeletedAt()).isNotNull();
    }

    @DisplayName("관리자 공지사항 삭제 실패 - 찾을 수 없는 공지사항")
    @Test
    public void deleteAdminNoticeNotFoundTest() {
        // given 준비
        // 메서드 모의
        Mockito.when(noticeRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // When 실행
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminNoticeService.deleteNotice(noticeId, getMemberData());
        });

        // then
        // (예상값, 발생한 상태 코드값)
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_NOTICE.getMessage(), exception.getMessage());
    }

    private Page<Notice> getNotices() {
        List<Notice> list = List.of(this.getNoticeData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Notice>(list, pageable, list.size());
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
                .name(name)
                .nickname(nickname)
                .build();
    }
}

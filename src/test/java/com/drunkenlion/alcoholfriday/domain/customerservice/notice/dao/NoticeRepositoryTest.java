package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[NoticeRepositoryTest] 공지사항 QueryDsl JPA Test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE notice AUTO_INCREMENT = 1").executeUpdate();

        Member member = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Notice notice = noticeRepository.save(
                Notice.builder()
                        .title("test title1")
                        .content("test content")
                        .member(member)
                        .status(NoticeStatus.PUBLISHED)
                        .build());
        noticeRepository.save(notice);

        Notice notice2 = noticeRepository.save(
                Notice.builder()
                        .title("test title1")
                        .content("test content")
                        .member(member)
                        .status(NoticeStatus.PUBLISHED)
                        .build());
        noticeRepository.save(notice2);

        Notice notice3 = noticeRepository.save(
                Notice.builder()
                        .title("test title3")
                        .content("test content")
                        .member(member)
                        .status(NoticeStatus.PUBLISHED)
                        .build());
        noticeRepository.save(notice3);

        Notice notice4 = noticeRepository.save(
                Notice.builder()
                        .title("test title4")
                        .content("test content")
                        .member(member)
                        .status(NoticeStatus.PUBLISHED)
                        .build());
        noticeRepository.save(notice4);

        Notice notice5 = noticeRepository.save(
                Notice.builder()
                        .title("테스트 제목5")
                        .content("테스트 내용5")
                        .member(member)
                        .status(NoticeStatus.DRAFT)
                        .build());
        noticeRepository.save(notice5);

        Notice notice6 = noticeRepository.save(
                Notice.builder()
                        .title("테스트 제목6")
                        .content("테스트 내용6")
                        .member(member)
                        .status(NoticeStatus.PUBLISHED)
                        .deletedAt(LocalDateTime.now())
                        .build());
        noticeRepository.save(notice6);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        noticeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공")
    void findNoticesTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<String> keywordType = new ArrayList<>();
        keywordType.add("title");
        keywordType.add("content");
        String keyword = "";

        Page<Notice> findNotices = noticeRepository.findNotices(pageable, keyword, keywordType);

        assertThat(findNotices.getContent()).isInstanceOf(List.class);
        assertThat(findNotices.getContent().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("공지사항 목록 검색 성공")
    void findNoticesSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<String> keywordType = new ArrayList<>();
        keywordType.add("title");
        keywordType.add("content");
        String keyword = "title1";

        Page<Notice> findNotices = noticeRepository.findNotices(pageable, keyword, keywordType);

        assertThat(findNotices.getContent()).isInstanceOf(List.class);
        assertThat(findNotices.getContent().size()).isEqualTo(2);
    }
}

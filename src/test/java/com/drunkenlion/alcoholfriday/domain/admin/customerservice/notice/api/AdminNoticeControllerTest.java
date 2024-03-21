package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.api;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@Transactional
@WithAccount
@SpringBootTest
public class AdminNoticeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Notice notice1 = noticeRepository.save(
                Notice.builder()
                        .title("test title")
                        .content("test content")
                        .status(NoticeStatus.PUBLISHED)
                        .member(member)
                        .build());
        noticeRepository.save(notice1);

        Notice notice2 = noticeRepository.save(
                Notice.builder()
                        .title("test title2")
                        .content("test content2")
                        .status(NoticeStatus.PUBLISHED)
                        .member(member)
                        .build());
        noticeRepository.save(notice2);

        Notice notice3 = noticeRepository.save(
                Notice.builder()
                        .title("테스트 제목3")
                        .content("테스트 내용3")
                        .status(NoticeStatus.PUBLISHED)
                        .member(member)
                        .build());
        noticeRepository.save(notice3);

        Notice notice4 = noticeRepository.save(
                Notice.builder()
                        .title("테스트 제목4")
                        .content("테스트 내용3")
                        .status(NoticeStatus.PUBLISHED)
                        .member(member)
                        .build());
        noticeRepository.save(notice4);

        Notice notice5 = noticeRepository.save(
                Notice.builder()
                        .title("테스트 제목5")
                        .content("테스트 내용3")
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

    @DisplayName("공지사항 목록 검색 성공")
    @Test
    void getAdminNoticesSearchTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/notices")
                        .param("keyword", "3")
                        .param("keywordType", "content")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("getNotices"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].memberId", notNullValue()))
                .andExpect(jsonPath("$.data[0].memberName", notNullValue()))
                .andExpect(jsonPath("$.data[0].memberNickname", notNullValue()))
                .andExpect(jsonPath("$.data[0].title", notNullValue()))
                .andExpect(jsonPath("$.data[0].content", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @DisplayName("공지사항 목록 조회 성공")
    @Test
    void getAdminNoticesTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("getNotices"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(6)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].memberId", notNullValue()))
                .andExpect(jsonPath("$.data[0].memberName", notNullValue()))
                .andExpect(jsonPath("$.data[0].memberNickname", notNullValue()))
                .andExpect(jsonPath("$.data[0].title", notNullValue()))
                .andExpect(jsonPath("$.data[0].content", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @DisplayName("공지사항 상세 조회 성공")
    @Test
    void getAdminNoticeTest() throws Exception {
        // given
        // noticeRepo 에서 첫 번째 아이템을 찾아와 notice 변수에 저장
        Notice notice = noticeRepository.findAll().get(0);

        // when
        // MockMvc "/v1/admin/notices/{notice.id}" 경로로 GET 요청을 보내고 resultActions 변수에 저장
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/notices/" + notice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("getNotice"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", notNullValue()))
                .andExpect(jsonPath("$.memberName", notNullValue()))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @DisplayName("공지사항 빈 엔티티 생성")
    @Test
    void initAdminNoticeTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "test title",
                                  "content": "test content",
                                  "memberId": 1
                                }
                                """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("initNotice"))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", notNullValue()))
                .andExpect(jsonPath("$.memberName", notNullValue()))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.title", is(nullValue())))
                .andExpect(jsonPath("$.content", is(nullValue())))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @DisplayName("공지사항 이미지 등록")
    @Test
    void saveAdminNoticeImageTest() throws Exception {
        Notice notice = noticeRepository.findAll().get(0);

        MockMultipartFile file = JsonConvertor.getMockImg("file");
        String entityDomain = "https://alcohol.friday.image.bucket.kr.object.ncloudstorage.com/notice";
        String entityId = notice.getId() + "_";

        ResultActions resultActions = mvc
                .perform(multipart("/v1/admin/notices/" + notice.getId())
                        .file(file)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("saveNoticeImage"))
                .andExpect(content().string(containsString(entityDomain)))
                .andExpect(content().string(containsString(entityId)));
    }

    @DisplayName("공지사항 수정 성공")
    @Test
    void modifyAdminNoticeTest() throws Exception {
        Notice notice = noticeRepository.findAll().get(0);

        ResultActions resultActions = mvc
                .perform(put("/v1/admin/notices/" + notice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "test title modified",
                                  "content": "test content modified"
                                }
                                """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("modifyNotice"))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", notNullValue()))
                .andExpect(jsonPath("$.memberName", notNullValue()))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @DisplayName("공지사항 삭제 성공")
    @Test
    void deleteAdminNoticeTest() throws Exception {
        Notice notice = noticeRepository.findAll().get(0);

        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/notices/" + notice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminNoticeController.class))
                .andExpect(handler().methodName("deleteNotice"));
    }
}

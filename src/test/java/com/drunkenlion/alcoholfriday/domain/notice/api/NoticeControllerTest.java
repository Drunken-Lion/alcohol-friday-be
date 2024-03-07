package com.drunkenlion.alcoholfriday.domain.notice.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.api.NoticeController;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class NoticeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = Member.builder()
                .email("smileby95@nate.com")
                .provider(ProviderType.KAKAO)
                .name("김태섭")
                .nickname("seop")
                .role(ADMIN)
                .phone(1041932693L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        memberRepository.save(member);

        Notice notice = noticeRepository.save(
                Notice.builder()
                        .title("test title")
                        .content("test content")
                        .member(member)
                        .build());
        noticeRepository.save(notice);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        noticeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("공지사항 목록 조회 성공")
    @Test
    void getNoticesTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(NoticeController.class))
                .andExpect(handler().methodName("getNotices"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].title", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @DisplayName("공지사항 상세 조회 성공")
    @Test
    void getNoticeTest() throws Exception {

        Notice notice = noticeRepository.findAll().get(0);

        ResultActions resultActions = mvc
                .perform(get("/v1/notices/" + notice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(NoticeController.class))
                .andExpect(handler().methodName("getNotice"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.member", notNullValue()))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)));
    }
}
